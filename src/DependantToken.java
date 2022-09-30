public class DependantToken extends RecursiveToken {
    private final Definition dependency;

    public DependantToken(TokenType tokenType, String content, Definition dependency) {
        super(tokenType, content, dependency);
        this.dependency = dependency;
    }

    public Definition getDependency() {
        return dependency;
    }
}
