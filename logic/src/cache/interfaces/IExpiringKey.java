package cache.interfaces;
import java.util.concurrent.Delayed;

public interface IExpiringKey<K> extends Delayed {
    K getKey();
    void renew();
}
