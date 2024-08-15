package cell.observability;

import cell.observability.interfaces.IObservabilityHandler;
import position.interfaces.IPosition;

import java.util.*;
import java.util.function.Consumer;

public abstract class ObservabilityHandler implements IObservabilityHandler<Subject> {
    protected Map<IPosition, Subject> position2Cell = new HashMap<>();
    protected Subject parent;

    public ObservabilityHandler(Subject parent) {
        this.parent = parent;
    }

    @Override
    public final void add(Subject subject) { position2Cell.put(subject.getPosition(), subject); }

    @Override
    public final void remove(Subject subject) { position2Cell.remove(subject.getPosition()); }

    @Override
    public final String toString() {
        return String.format("List of observers: %s", position2Cell.keySet());
    }

    @Override
    public final int size() { return position2Cell.size(); }

    @Override
    public final Set<IPosition> keySet() { return Set.copyOf(position2Cell.keySet()); }

    @Override
    public final Collection<Subject> getValues() { return position2Cell.values(); }

    @Override
    public abstract void clear();

    protected void safeExecute(Consumer<Subject> updateOperation) {
        new ArrayList<>(position2Cell.values()).forEach(updateOperation);
    }
}
