public class DependantToken extends RecursiveToken {
    private final String dependency;

    public DependantToken(TokenType tokenType, String content, String dependency) {
        super(tokenType, content, dependency);
        this.dependency = dependency;
    }

    public String getDependency() {
        return dependency;
    }
}
