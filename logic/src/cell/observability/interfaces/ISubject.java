package cell.observability.interfaces;

import cell.observability.Observers;
import position.interfaces.IPosition;

public interface ISubject extends Cloneable {
    Observers getObservers();
    void onUpdateFail();
    void addObservable(ISubject observed);
    IPosition getPosition();
    void revertUpdate();
    void removeObserver(ISubject observer);
    void removeObservable(ISubject observed);
    void addObserver(ISubject observer);
}
