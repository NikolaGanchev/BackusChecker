public class RecursiveToken extends Token {

    private final String name;

    public RecursiveToken(TokenType tokenType, String content, String definitionName) {
        super(tokenType, content);
        this.name = definitionName;
    }

    public String stripDefinition() {
        return this.getContent().replace(name, "");
    }

    public String getStartingSequence() {
        return this.getContent().substring(0, this.getContent().indexOf(name));
    }

    public String getEndingSequence() {
        return this.getContent().substring((getStartingSequence().length() + name.length()));
    }
}
