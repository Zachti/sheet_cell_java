package cache;

import cache.interfaces.ICache;
import cache.interfaces.IExpiringKey;
import cache.interfaces.ISupplier;

import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Objects.requireNonNull;

public final class Cache<K, V> implements ICache<K, V> { // small in memory LRU cache
    private final LinkedHashMap<K, V> cacheMap;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final DelayQueue<IExpiringKey<K>> delayQueue = new DelayQueue<>();
    private final Map<K, IExpiringKey<K>> expiringKeys = new HashMap<>();
    private final long defaultExpiryAfter;
    private final TemporalUnit defaultExpiryUnit;
    private final static int MAX_SIZE = 10;

    public Cache(long defaultExpiryAfter, TemporalUnit defaultExpiryUnit) {
        this.defaultExpiryAfter = defaultExpiryAfter;
        this.defaultExpiryUnit = defaultExpiryUnit;
        this.cacheMap = new LinkedHashMap<>(MAX_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                boolean shouldRemove = size() > MAX_SIZE;
                Optional.of(shouldRemove)
                        .filter(Boolean::booleanValue)
                        .ifPresent(_ -> evictOldestEntry(eldest));
                return shouldRemove;
            }
        };
    }

    @Override
    public void put(K key, V value) {
        requireNonNull(key);
        rwl.writeLock().lock();
        try {
            doCleanup();
            internalPutValue(key, value);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public V get(K key) {
        requireNonNull(key);
        rwl.readLock().lock();
        try {
            doCleanup();
            return internalGetValue(key);
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public V getOrElseUpdate(K key, ISupplier<V> supplier) {
        requireNonNull(key);
        rwl.readLock().lock();
        try {
            doCleanup();
            return Optional.ofNullable(internalGetValue(key))
                    .orElseGet(() -> internalPutValue(key, supplier.supply()));
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        rwl.writeLock().lock();
        try {
            cacheMap.clear();
            delayQueue.clear();
            expiringKeys.clear();
        } finally {
            rwl.writeLock().unlock();
        }
    }
    
    private V internalPutValue(K key, V value) {
        IExpiringKey<K> expiringKey = new ExpiringKey<>(key, defaultExpiryAfter, defaultExpiryUnit);
        IExpiringKey<K> oldKey = expiringKeys.put(key, expiringKey);
        Optional.ofNullable(oldKey).ifPresent(delayQueue::remove);
        delayQueue.offer(expiringKey);
        cacheMap.put(key, value);
        return value;
    }
    
    private V internalGetValue(K key) {
        IExpiringKey<K> expiringKey = expiringKeys.get(key);
        Optional.ofNullable(expiringKey).ifPresent(IExpiringKey::renew);
        return cacheMap.get(key);
    }
    
    private void doCleanup() {
        IExpiringKey<K> expiringKey;
        while ((expiringKey = delayQueue.poll()) != null) { internalRemoveKey(expiringKey); }
    }

    private void evictOldestEntry(Map.Entry<K, V> eldest) {
        IExpiringKey<K> expiringKey = expiringKeys.get(eldest.getKey());
        Optional.ofNullable(expiringKey)
                .ifPresent(this::internalRemoveKey);
    }

    private void internalRemoveKey(IExpiringKey<K> expiringKey) {
        delayQueue.remove(expiringKey);
        expiringKeys.remove(expiringKey.getKey());
    }
}
