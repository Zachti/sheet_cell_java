package cell.observability;

import cell.observability.interfaces.IObservable;
import cell.observability.interfaces.IObserver;
import cell.observability.interfaces.ISubject;
import position.interfaces.IPosition;
import store.SetContextStore;

public abstract class Subject implements ISubject,  IObservable<ISubject>, IObserver<ISubject> {

    protected Observers observers = new Observers(this);
    protected Observables observables = new Observables(this);

    @Override
    public final void addObserver(ISubject observer) { observers.add(observer); }

    @Override
    public final void removeObserver(ISubject observer) { observers.remove(observer); }

    @Override
    public final void addObservable(ISubject observed) { observables.add(observed); }

    @Override
    public final void removeObservable(ISubject observed) { observables.remove(observed); }

    @Override
    public final int getObserversCount() { return observers.size(); }

    @Override
    public Subject clone() {
        try {
            Subject clone = (Subject) super.clone();
            clone.observables = new Observables(observables.parent);
            clone.observers = new Observers(observers.parent);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public final Observers getObservers() { return observers; }

    @Override
    public final void onUpdateFail() {
        observables.clear();
        SetContextStore.getSubjectSetStore().getContext().forEach(c -> {
            c.addObserver(this);
            observables.add(c);
        });
        revertUpdate();
    }

    protected void onSubjectUpdate() { observables.clear(); }

    public abstract IPosition getPosition();

    public abstract void revertUpdate();
}
