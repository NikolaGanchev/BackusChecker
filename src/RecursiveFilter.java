import java.util.function.Function;

public class RecursiveFilter implements Filter{
    private final String startingSequence;
    private final String endingSequence;
    private final Function<String, Boolean> recFunction;

    public RecursiveFilter(String startingSequence, String endingSequence, Function<String, Boolean> recFunction) {
        this.startingSequence = startingSequence;
        this.endingSequence = endingSequence;
        this.recFunction = recFunction;
    }

    @Override
    public boolean isSafetyCheck() {
        return false;
    }

    @Override
    public boolean check(String stringToCheck) {
        if (stringToCheck.substring(0, startingSequence.length()).equals(startingSequence)
            && stringToCheck.substring(stringToCheck.length() - endingSequence.length())
                .equals(endingSequence)) {

            return recFunction.apply(stringToCheck.substring(startingSequence.length(),
                    stringToCheck.length() - endingSequence.length()));
        } else {
            return false;
        }
    }
}
