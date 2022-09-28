public class Token {
    private final TokenType tokenType;
    private final String content;

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getContent() {
        return content;
    }

    public Token(TokenType tokenType, String content) {
        this.tokenType = tokenType;
        this.content = content;
    }
}
