package cell.observability.interfaces;

import cell.observability.Observers;

public interface ISubject extends Cloneable {
    Observers getObservers();
    void onUpdateFail();
}
