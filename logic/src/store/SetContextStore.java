package store;

import cell.Cell;
import cell.observability.interfaces.ISubject;
import store.interfaces.IStore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SetContextStore<T extends ISubject> { // both stores are singletons

    private final IStore<Set<T>> contextStore = new ContextStore<>();
    private static final SetContextStore<Cell> CELL_SET_CONTEXT_STORE = new SetContextStore<>();
    private static final SetContextStore<ISubject> SUBJECT_SET_CONTEXT_STORE = new SetContextStore<>();

    public void setContext(List<T> cells) { contextStore.set(new HashSet<>(cells)); }

    public Set<T> getContext() { return contextStore.get(); }

    public void clearContext() { contextStore.clear(); }

    private SetContextStore() {}

    public static SetContextStore<Cell> getCellSetStore() { return CELL_SET_CONTEXT_STORE; }

    public static SetContextStore<ISubject> getSubjectSetStore() { return SUBJECT_SET_CONTEXT_STORE; }
}
