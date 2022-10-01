package backus;

import backus.generators.RecursiveGenerator;
import backus.generators.SimpleGenerator;
import backus.generators.Generator;
import backus.tokens.RecursiveToken;
import backus.tokens.Token;

import java.util.*;

public class BackusGenerator {
    private final List<Generator<String>> generators;
    private final Definition definition;
    private final EvenOddRule evenOddRule;

    protected BackusGenerator(Definition name, List<Generator<String>> generators, EvenOddRule evenOddRule) {
        this.definition = name;
        this.generators = generators;
        this.evenOddRule = evenOddRule;
    }

    public Definition getDefinition() {
        return definition;
    }

    public Collection<String> generate(int maximumLength) {
        Set<String> result = new HashSet<>();

        if (!evenOddRule.satisfiesRule(maximumLength)) {
            return result;
        }

        for (Generator<String> generator: generators) {
            if (generator.isSimpleGenerator()) {
                generate(maximumLength, result, generator.generate(null));
            }
        }

        return result;
    }

    private void generate(int maximumLength, Set<String> destination, String lastResult) {
        if (maximumLength < lastResult.length()) {
            return;
        }
        if (maximumLength == lastResult.length()) {
            destination.add(lastResult);
            return;
        }

        for (Generator<String> generator: generators) {
            if (!generator.isSimpleGenerator()) {
                generate(maximumLength, destination, generator.generate(lastResult));
            }
        }
    }

    public static Builder Builder(String name, String definition) {
        return new Builder(name, definition);
    }


    public static class Builder {
        private Parser parser;

        private Builder(String name, String template) {
            this.parser = new Parser(Definition.fromName(name), template);
        }

        public BackusGenerator build() {
            List<Token> tokens = parser.parse();
            BackusGenerator backusGenerator = new BackusGenerator(
                    parser.getName(),
                    buildGenerators(tokens),
                    parser.getEvenOddRule(tokens));
            return backusGenerator;
        }

        private List<Generator<String>> buildGenerators(List<Token> tokens) {
            ArrayList<Generator<String>> generators = new ArrayList<>();

            for (Token token: tokens) {
                switch(token.getTokenType()) {
                    case SIMPLE -> generators.add(new SimpleGenerator(token.getContent()));
                    case RECURSIVE -> {
                        RecursiveToken recToken = (RecursiveToken) token;

                        generators.add(new RecursiveGenerator(
                                recToken.getStartingSequence(),
                                recToken.getEndingSequence()));
                    }
                }
            }

            return generators;
        }

        public Builder setParser(Parser parser) {
            this.parser = parser;
            return this;
        }
    }
}
