package cell.observability.interfaces;

import position.interfaces.IPosition;

import java.util.Collection;
import java.util.Set;

public interface IObservabilityHandler<T> {
    void add(T cell);
    void remove(T cell);
    int size();
    Set<IPosition> keySet();
    Collection<ISubject> getValues();
    void clear();
}
