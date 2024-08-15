package store.interfaces;

public interface IStore<T> {
    void set(T value);
    T get();
    void clear();
}
