public class SimpleEqualityFilter implements Filter{

    private final String stringToCheckAgainst;

    public SimpleEqualityFilter(String stringToCheckAgainst) {
        this.stringToCheckAgainst = stringToCheckAgainst;
    }

    @Override
    public boolean isSafetyCheck() {
        return false;
    }

    @Override
    public boolean check(String stringToCheck) {
        return stringToCheck.equals(stringToCheckAgainst);
    }
}
