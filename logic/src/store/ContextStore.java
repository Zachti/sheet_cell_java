package store;

import store.interfaces.IStore;

final class ContextStore<T> implements IStore<T> {
    private final ThreadLocal<T> store = new ThreadLocal<>();

    @Override
    public void set(T context) { store.set(context); }

    @Override
    public T get() { return store.get(); }

    @Override
    public void clear() { store.remove(); }
}
