package cell.observability;

import cell.observability.interfaces.ISubject;

public final class Observers extends ObservabilityHandler {

    public Observers(ISubject parent) { super(parent); }

    @Override
    public void clear() {
        safeExecute(observer -> observer.removeObservable(parent));
        position2Cell.clear();
    }
}
