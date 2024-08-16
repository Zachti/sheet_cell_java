package cell.observability;

import cell.observability.interfaces.IObservabilityHandler;
import cell.observability.interfaces.ISubject;
import position.interfaces.IPosition;

import java.util.*;
import java.util.function.Consumer;

public abstract class ObservabilityHandler implements IObservabilityHandler<ISubject> {
    protected Map<IPosition, ISubject> position2Cell = new HashMap<>();
    protected ISubject parent;

    public ObservabilityHandler(ISubject parent) {
        this.parent = parent;
    }

    @Override
    public final void add(ISubject subject) { position2Cell.put(subject.getPosition(), subject); }

    @Override
    public final void remove(ISubject subject) { position2Cell.remove(subject.getPosition()); }

    @Override
    public final String toString() {
        return String.format("%s", position2Cell.keySet());
    }

    @Override
    public final int size() { return position2Cell.size(); }

    @Override
    public final Set<IPosition> keySet() { return Set.copyOf(position2Cell.keySet()); }

    @Override
    public final Collection<ISubject> getValues() { return position2Cell.values(); }

    @Override
    public abstract void clear();

    protected void safeExecute(Consumer<ISubject> updateOperation) {
        new ArrayList<>(position2Cell.values()).forEach(updateOperation);
    }
}
