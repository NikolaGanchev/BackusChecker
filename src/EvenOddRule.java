public enum EvenOddRule {
    EVEN,
    ODD,
    NONE;

    public boolean satisfiesRule(int number) {
        switch (this) {
            case EVEN -> {
                return number % 2 == 0;
            }
            case ODD -> {
                return number % 2 != 0;
            }
            default -> {
                return true;
            }
        }
    }
}
