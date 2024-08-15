package cell.observability;

public final class Observables extends ObservabilityHandler {

    public Observables(Subject parent) { super(parent); }

    @Override
    public void clear() {
        safeExecute(observable -> observable.removeObserver(parent));
        position2Cell.clear();
    }
}
