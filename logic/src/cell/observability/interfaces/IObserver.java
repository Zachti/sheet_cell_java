package cell.observability.interfaces;

public interface IObserver<T> {
    void addObservable(T observable);
    void removeObservable(T observable);
    int getObserversCount();
}
