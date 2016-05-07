package nucleus.example.util;

public class Delayed<T> {

    public interface Factory<T> {
        T create();
    }

    private final Factory<T> factory;
    private T value;
    private boolean initialized;

    public Delayed(Factory<T> factory) {
        this.factory = factory;
    }

    public synchronized T get() {
        if (!initialized) {
            value = factory.create();
            initialized = true;
        }
        return value;
    }
}
