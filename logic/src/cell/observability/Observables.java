package cell.observability;

import cell.observability.interfaces.ISubject;

public final class Observables extends ObservabilityHandler {

    public Observables(ISubject parent) { super(parent); }

    @Override
    public void clear() {
        safeExecute(observable -> observable.removeObserver(parent));
        position2Cell.clear();
    }
}
