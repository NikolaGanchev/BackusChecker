public class EvenOddFilter implements Filter {
    private final EvenOddRule evenOddRule;

    public EvenOddFilter(EvenOddRule evenOddRule) {
        this.evenOddRule = evenOddRule;
    }

    @Override
    public boolean isSafetyCheck() {
        return true;
    }

    @Override
    public boolean check(String stringToCheck) {
        switch (evenOddRule) {
            case EVEN -> {
                return stringToCheck.length() % 2 == 0;
            }
            case ODD -> {
                return stringToCheck.length() % 2 != 0;
            }
        }

        return true;
    }
}
