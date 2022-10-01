package backus.tokens;

import backus.Definition;
import backus.TokenType;

public class RecursiveToken extends Token {

    private final Definition name;

    public RecursiveToken(TokenType tokenType, String content, Definition definition) {
        super(tokenType, content);
        this.name = definition;
    }

    public String stripDefinition() {
        return this.getContent().replace(name.getFullDefinition(), "");
    }

    public String getStartingSequence() {
        return this.getContent().substring(0, this.getContent().indexOf(name.getFullDefinition()));
    }

    public String getEndingSequence() {
        return this.getContent().substring((getStartingSequence().length() + name.getFullDefinition().length()));
    }
}
