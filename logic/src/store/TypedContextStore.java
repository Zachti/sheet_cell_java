package store;

import cell.observability.Subject;
import sheet.Sheet;
import store.interfaces.IStore;

public final class TypedContextStore<T> { // every store is a singleton
    private final IStore<T> contextStore = new ContextStore<>();
    private static final TypedContextStore<Sheet> SHEET_CONTEXT_STORE = new TypedContextStore<>();
    private static final TypedContextStore<Subject> SUBJECT_CONTEXT_STORE = new TypedContextStore<>();


    public void setContext(T value) { contextStore.set(value); }

    public T getContext() { return contextStore.get(); }

    public void clearContext() { contextStore.clear(); }

    private TypedContextStore() {}

    public static TypedContextStore<Sheet> getSheetStore() { return SHEET_CONTEXT_STORE; }
    public static TypedContextStore<Subject> getSubjectStore() { return SUBJECT_CONTEXT_STORE; }

}
