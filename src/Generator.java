
public interface Generator<T> {
    // Трябва да се изпълни единствено за прости случаи
    boolean isSimpleGenerator();

    T generate(T result);
}
