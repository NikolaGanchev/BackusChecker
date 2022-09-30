
public class SimpleGenerator implements Generator<String> {

    private String value;

    public SimpleGenerator(String value) {
        this.value = value;
    }

    @Override
    public boolean isSimpleGenerator() {
        return true;
    }

    @Override
    public String generate(String result) {
        return this.value;
    }


}
