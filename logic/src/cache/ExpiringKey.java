package cache;

import cache.interfaces.IExpiringKey;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

class ExpiringKey<K> implements IExpiringKey<K> {
    private final K key;
    private long expireAfter;
    private TemporalUnit expiryUnit;
    private Instant startTime;

    public ExpiringKey(K key) {
        this.key = key;
    }

    public ExpiringKey(K key, long expireAfter, TemporalUnit expiryUnit) {
        this(key);
        this.expiryUnit = expiryUnit;
        this.startTime = Instant.now();
        this.expireAfter = expireAfter;
    }

    public K getKey() {
        return key;
    }

    public void renew() {
        this.startTime = Instant.now();
    }

    @Override
    public long getDelay(TimeUnit timeUnit) {
        long diff = startTime == null ? 0 : Duration.between(Instant.now(),
                startTime.plus(expireAfter, expiryUnit)).toMillis();
        return timeUnit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed that) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), that.getDelay(TimeUnit.MILLISECONDS));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return key.equals(((ExpiringKey<?>) o).key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "ExpiringKey=" + key;
    }
}
