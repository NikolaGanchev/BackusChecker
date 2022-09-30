import java.util.*;
import java.util.function.Function;

public class BackusChecker {
    private List<Filter> filters;
    private final Definition definition;
    private final HashMap<Definition, BackusChecker> dependencies = new HashMap<>();

    protected BackusChecker(Definition name, List<Filter> filters, Collection<Definition> dependencyNames) {
        this.definition = name;
        this.filters = filters;

        for (Definition dependencyName: dependencyNames) {
            dependencies.put(dependencyName, null);
        }
    }

    public void registerDependency(BackusChecker checker) {
        if (dependencies.containsKey(checker.getDefinition())) {
            dependencies.put(checker.getDefinition(), checker);
            refreshDependencies();
        } else {
            throw new IllegalArgumentException("Cannot register undeclared dependency");
        }
    }

    private void refreshDependencies() {
        for (Filter filter: filters) {
            if (filter instanceof DependantFilter dependantFilter) {
                BackusChecker dependency = dependencies.get(dependantFilter.getDependencyDefinition());
                if (dependency != null) {
                    dependantFilter.setDependency(dependency);
                }
            }
        }
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public Definition getDefinition() {
        return definition;
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
        private Parser parser;

        private Builder(String name, String template) {
            this.parser = new Parser(Definition.fromName(name), template);
        }

        public BackusChecker build() {
            BackusChecker backusChecker = new BackusChecker(parser.getName(), null, parser.getDependencies());

            backusChecker.setFilters(buildFilters(parser.parse(), backusChecker::check));
            return backusChecker;
        }

        private List<Filter> buildFilters(List<Token> tokens, Function<String, Boolean> recFunction) {
            ArrayList<Filter> filters = new ArrayList<>();

            int minimumLength = parser.getMinimumViableLength(tokens);

            filters.add(new MinimumLengthFilter(minimumLength));

            EvenOddRule evenOddRule = parser.getEvenOddRule(tokens);

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
            parser.addDependency(dependencyName);

            return this;
        }

        public Builder expectDependencies(Collection<String> dependencyNames) {
            parser.addDependencies(dependencyNames);

            return this;
        }

        public Builder setParser(Parser parser) {
            this.parser = parser;
            return this;
        }
    }
}
