package store;

import cell.observability.interfaces.ISubject;
import range.IRange;
import sheet.interfaces.ISheet;
import store.interfaces.IStore;

import java.util.Map;

public final class TypedContextStore<T> { // every store is a singleton
    private final IStore<T> contextStore = new ContextStore<>();
    private static final TypedContextStore<ISheet> SHEET_CONTEXT_STORE = new TypedContextStore<>();
    private static final TypedContextStore<ISubject> SUBJECT_CONTEXT_STORE = new TypedContextStore<>();
    private static final TypedContextStore<Map<String, IRange>> RANGES_STORE = new TypedContextStore<>();

    public void setContext(T value) { contextStore.set(value); }

    public T getContext() { return contextStore.get(); }

    public void clearContext() { contextStore.clear(); }

    private TypedContextStore() {}

    public static TypedContextStore<ISheet> getSheetStore() { return SHEET_CONTEXT_STORE; }
    public static TypedContextStore<ISubject> getSubjectStore() { return SUBJECT_CONTEXT_STORE; }
    public static TypedContextStore<Map<String, IRange>> getRangesStore() { return RANGES_STORE; }
}
