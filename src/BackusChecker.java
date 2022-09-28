import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BackusChecker {
    private List<Filter> filters;
    private final String name;

    protected BackusChecker(String name, List<Filter> filters) {
        this.name = name;
        this.filters = filters;
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
        private final String name;

        private Builder(String name, String definition) {
            this.name = name;

            String[] values = definition.split("[|]");
            String definedName = getDefinedName();

            for (String value : values) {
                TokenType type = TokenType.SIMPLE;

                if (value.contains(definedName)) {
                    type = TokenType.RECURSIVE;
                    tokens.add(new RecursiveToken(type, value, definedName));
                    // Връщаме, за да избегнем възможността в една дефиниция да има и собствената, и друга дефиниция
                    continue;
                }

                tokens.add(new Token(type, value));
            }
        }

        public BackusChecker build() {
            BackusChecker backusChecker = new BackusChecker(name, null);
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
                }
            }

            return filters;
        }

        private EvenOddRule getEvenOddRule() {
            ArrayList<Integer> simpleSizes = new ArrayList<>();
            ArrayList<Integer> recSizes = new ArrayList<>();

            for (Token token: tokens) {
                switch (token.getTokenType()) {
                    case SIMPLE -> simpleSizes.add(token.getContent().length());
                    case RECURSIVE -> recSizes.add(((RecursiveToken) token).stripRecursive().length());
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
            }

            if (minimumLength == -1) {
                throw new IllegalArgumentException("Definition doesn't containt concrete cases");
            }

            return minimumLength;
        }

        private String getDefinedName() {
            return "<" + name + ">";
        }

        // Връща единствено първата поява на друга дефиниция
        private String getDependency(String stringToCheck) {
            Pattern pattern = Pattern.compile("<.+>");
            Matcher matcher = pattern.matcher(stringToCheck);

            ArrayList<String> matches = matcher.results().map(MatchResult::group).collect(
                    Collectors.toCollection(ArrayList::new)
            );

            matches.removeIf((str) -> str.substring(1, str.length() - 1).equals(name));

            String result = matches.get(0);

            if (result == null) return null;

            return result.substring(1, result.length() - 1);
        }
    }
}
