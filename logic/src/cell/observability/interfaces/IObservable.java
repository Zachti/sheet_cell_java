package cell.observability.interfaces;

public interface IObservable<T> {
    void addObserver(T observer);
    void removeObserver(T observer);
}
