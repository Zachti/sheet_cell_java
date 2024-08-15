package store;

import cell.Cell;
import cell.observability.Subject;
import store.interfaces.IStore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SetContextStore<T extends Subject> { // both stores are singletons

    private final IStore<Set<T>> contextStore = new ContextStore<>();
    private static final SetContextStore<Cell> CELL_SET_CONTEXT_STORE = new SetContextStore<>();
    private static final SetContextStore<Subject> SUBJECT_SET_CONTEXT_STORE = new SetContextStore<>();

    public void setContext(List<T> cells) { contextStore.set(new HashSet<>(cells)); }

    public Set<T> getContext() { return contextStore.get(); }

    public void clearContext() { contextStore.clear(); }

    private SetContextStore() {}

    public static SetContextStore<Cell> getCellSetStore() { return CELL_SET_CONTEXT_STORE; }

    public static SetContextStore<Subject> getSubjectSetStore() { return SUBJECT_SET_CONTEXT_STORE; }
}
