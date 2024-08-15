package cache.interfaces;

public interface ICache<K,V> {
    V get(K key);
    void put(K key, V value);
    V getOrElseUpdate(K key, ISupplier<V> supplier);
    void clear();
}
