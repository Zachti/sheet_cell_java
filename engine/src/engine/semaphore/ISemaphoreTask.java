package engine.semaphore;

@FunctionalInterface
public interface ISemaphoreTask<T> {
    T execute() throws Exception;
}
