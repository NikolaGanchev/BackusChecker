import java.util.*;
import java.util.function.Function;

public class BackusChecker {
    private List<Filter> filters;
    private final String name;
    private final HashMap<String, BackusChecker> dependencies = new HashMap<>();

    protected BackusChecker(String name, List<Filter> filters, Collection<String> dependencyNames) {
        this.name = name;
        this.filters = filters;

        for (String dependencyName: dependencyNames) {
            dependencies.put(dependencyName, null);
        }
    }

    public void registerDependency(BackusChecker checker) {
        if (dependencies.containsKey(checker.name)) {
            dependencies.put(checker.name, checker);
            refreshDependencies();
        } else {
            throw new IllegalArgumentException("Cannot register undeclared dependency");
        }
    }

    private void refreshDependencies() {
        for (Filter filter: filters) {
            if (filter instanceof DependantFilter dependantFilter) {
                BackusChecker dependency = dependencies.get(dependantFilter.getDependencyName());
                if (dependency != null) {
                    dependantFilter.setDependency(dependency);
                }
            }
        }
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public String getName() {
        return name;
    }

    public boolean check(String stringToCheck) {
        for (Filter filter : filters) {

            if (filter.isSafetyCheck()) {
                if (!filter.check(stringToCheck)) return false;
                else continue;
            }

            if(filter.check(stringToCheck)) return true;
        }

        return false;
    }

    public static Builder Builder(String name, String definition) {
        return new Builder(name, definition);
    }


    public static class Builder {
        private final ArrayList<Token> tokens = new ArrayList<>();
        private final Set<String> dependencies = new HashSet<>();
        private final String name;
        private final String definition;

        private Builder(String name, String definition) {
            this.name = name;
            this.definition = definition;
        }

        private void parse() {
            String[] values = definition.split("[|]");
            String definedName = getDefinedName(name);

            tokenLoop:
            for (String value : values) {
                TokenType type = TokenType.SIMPLE;

                if (value.contains(definedName)) {
                    type = TokenType.RECURSIVE;
                    tokens.add(new RecursiveToken(type, value, definedName));
                    // Връщаме, за да избегнем възможността в една дефиниция да има и собствената, и друга дефиниция
                    continue tokenLoop;
                }

                for (String dependency: dependencies) {
                    String dependencyDefinedName = getDefinedName(dependency);
                    if (value.contains(dependencyDefinedName)) {
                        type = TokenType.DEPENDANT;
                        tokens.add(new DependantToken(type, value, dependencyDefinedName));
                        continue tokenLoop;
                    }
                }

                tokens.add(new Token(type, value));
            }
        }

        public BackusChecker build() {
            BackusChecker backusChecker = new BackusChecker(name, null, dependencies);
            parse();
            backusChecker.setFilters(buildFilters(backusChecker::check));
            return backusChecker;
        }

        private List<Filter> buildFilters(Function<String, Boolean> recFunction) {
            ArrayList<Filter> filters = new ArrayList<>();

            int minimumLength = getMinimumViableLength();

            filters.add(new MinimumLengthFilter(minimumLength));

            EvenOddRule evenOddRule = getEvenOddRule();

            if (evenOddRule != EvenOddRule.NONE) {
                filters.add(new EvenOddFilter(evenOddRule));
            }

            for (Token token: tokens) {
                switch (token.getTokenType()) {
                    case SIMPLE -> filters.add(new SimpleEqualityFilter(token.getContent()));
                    case RECURSIVE -> {
                        RecursiveToken recToken = (RecursiveToken) token;
                        filters.add(new RecursiveFilter(
                                recToken.getStartingSequence(),
                                recToken.getEndingSequence(),
                                recFunction));
                    }
                    case DEPENDANT -> {
                        DependantToken depToken = (DependantToken) token;
                        filters.add(new DependantFilter(
                                depToken.getStartingSequence(),
                                depToken.getEndingSequence(),
                                depToken.getDependency()
                        ));
                    }
                }
            }

            return filters;
        }

        public Builder expectDependency(String dependencyName) {
            dependencies.add(dependencyName);

            return this;
        }

        public Builder expectDependencies(Collection<String> dependencyNames) {
            dependencies.addAll(dependencyNames);

            return this;
        }

        private EvenOddRule getEvenOddRule() {
            ArrayList<Integer> simpleSizes = new ArrayList<>();
            ArrayList<Integer> recSizes = new ArrayList<>();

            for (Token token: tokens) {
                switch (token.getTokenType()) {
                    case SIMPLE -> simpleSizes.add(token.getContent().length());
                    case RECURSIVE -> recSizes.add(((RecursiveToken) token).stripDefinition().length());
                    // Правилото не може да бъде безопасно определено, ако има външна дефиниция
                    case DEPENDANT -> {
                        return EvenOddRule.NONE;
                    }
                }
            }

            int evenAmountSimple = getAmountOfEven(simpleSizes);
            int evenAmountRec = getAmountOfEven(recSizes);

            if (simpleSizes.size() == evenAmountSimple && recSizes.size() == evenAmountRec) {
                return EvenOddRule.EVEN;
            } else if (simpleSizes.size() == 0 && recSizes.size() == 0) {
                return EvenOddRule.ODD;
            }

            return EvenOddRule.NONE;
        }

        private int getAmountOfEven(List<Integer> arr) {
            int evenAmount = 0;
            for (int size: arr) {
                if (size % 2 == 0) {
                    evenAmount++;
                }
            }

            return evenAmount;
        }

        private int getMinimumViableLength() {
            int minimumLength = -1;

            for (Token token: tokens) {
                if (token.getTokenType() == TokenType.SIMPLE) {
                    if (minimumLength == -1) {
                        minimumLength = token.getContent().length();
                        continue;
                    }

                    if (minimumLength > token.getContent().length()) {
                        minimumLength = token.getContent().length();
                    }
                }
                if (token.getTokenType() == TokenType.DEPENDANT) {
                    DependantToken dependantToken = (DependantToken) token;
                    if (minimumLength > dependantToken.stripDefinition().length()) {
                        minimumLength = dependantToken.stripDefinition().length();
                    }
                }
            }

            if (minimumLength == -1) {
                //Definition doesn't contain concrete cases
                for (Token token: tokens) {
                    if (token.getTokenType() == TokenType.RECURSIVE) {
                        RecursiveToken recursiveToken = (RecursiveToken) token;
                        if (minimumLength > recursiveToken.stripDefinition().length()) {
                            minimumLength = recursiveToken.stripDefinition().length();
                        }
                    }
                }
            }

            return minimumLength;
        }

        private String getDefinedName(String name) {
            return "<" + name + ">";
        }
    }
}
