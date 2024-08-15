package cell.observability;

public final class Observers extends ObservabilityHandler {

    public Observers(Subject parent) { super(parent); }

    @Override
    public void clear() {
        safeExecute(observer -> observer.removeObservable(parent));
        position2Cell.clear();
    }
}
