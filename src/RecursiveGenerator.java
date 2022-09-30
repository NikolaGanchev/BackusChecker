
public class RecursiveGenerator implements Generator<String> {
    private final String startingSequence;
    private final String endingSequence;

    public RecursiveGenerator(String startingSequence, String endingSequence) {
        this.startingSequence = startingSequence;
        this.endingSequence = endingSequence;
    }
    @Override
    public boolean isSimpleGenerator() {
        return false;
    }

    @Override
    public String generate(String result) {
        return this.startingSequence + result + this.endingSequence;
    }
}
