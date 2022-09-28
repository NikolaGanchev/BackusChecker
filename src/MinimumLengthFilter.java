public class MinimumLengthFilter implements Filter{
    private final int minimumLength;

    public MinimumLengthFilter(int minimumLength) {
        this.minimumLength = minimumLength;
    }

    @Override
    public boolean isSafetyCheck() {
        return true;
    }

    @Override
    public boolean check(String stringToCheck) {
        return !(stringToCheck.length() < minimumLength);
    }
}
