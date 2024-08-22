package engine.semaphore;

@FunctionalInterface
public interface SemaphoreTask {
    void execute() throws Exception;
}
