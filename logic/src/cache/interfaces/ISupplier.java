package cache.interfaces;

@FunctionalInterface
public interface ISupplier<V> {
    V supply();
}
