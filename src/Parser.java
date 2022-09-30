import java.util.*;

public class Parser {
    private final String template;
    private final Definition name;
    private final Set<Definition> dependencies = new HashSet<>();

    public Parser(Definition name, String template) {
        this.name = name;
        this.template = template;
    }

    public void addDependency(String dependencyName) {
        dependencies.add(Definition.fromName(dependencyName));
    }

    public void addDependencies(Collection<String> dependencyNames) {
        dependencies.addAll(dependencyNames.stream().map(Definition::fromName).toList());
    }

    public List<Token> parse() {
        String[] values = template.split("[|]");
        ArrayList<Token> tokens = new ArrayList<>();

        tokenLoop:
        for (String value : values) {
            TokenType type = TokenType.SIMPLE;

            if (value.contains(name.getFullDefinition())) {
                type = TokenType.RECURSIVE;
                tokens.add(new RecursiveToken(type, value, name));
                // Връщаме, за да избегнем възможността в една дефиниция да има и собствената, и друга дефиниция
                continue tokenLoop;
            }

            for (Definition dependency: dependencies) {
                if (value.contains(dependency.getFullDefinition())) {
                    type = TokenType.DEPENDANT;
                    tokens.add(new DependantToken(type, value, dependency));
                    continue tokenLoop;
                }
            }

            tokens.add(new Token(type, value));
        }

        return tokens;
    }

    public EvenOddRule getEvenOddRule(List<Token> tokens) {
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

    public int getMinimumViableLength(List<Token> tokens) {
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
            // Дефиницята не съдържа предопределени случаи
            for (Token token: tokens) {
                if (token.getTokenType() == TokenType.RECURSIVE) {
                    RecursiveToken recursiveToken = (RecursiveToken) token;
                    if (minimumLength == -1) {
                        minimumLength = token.getContent().length();
                        continue;
                    }
                    if (minimumLength > recursiveToken.stripDefinition().length()) {
                        minimumLength = recursiveToken.stripDefinition().length();
                        continue;
                    }
                }
                if (token.getTokenType() == TokenType.DEPENDANT) {
                    DependantToken dependantToken = (DependantToken) token;
                    if (minimumLength == -1) {
                        minimumLength = token.getContent().length();
                        continue;
                    }
                    if (minimumLength > dependantToken.stripDefinition().length()) {
                        minimumLength = dependantToken.stripDefinition().length();
                    }
                }
            }
        }

        return minimumLength;
    }

    public Definition getName() {
        return name;
    }

    public Set<Definition> getDependencies() {
        return dependencies;
    }
}
