package common.thread.job;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobQueue {
    private final ThreadPoolExecutor executor;

    public JobQueue(int poolSize, int queueSize) {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueSize);
        executor = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, queue);
        registerShutdownHook();
    }

    public void addJob(IJob job) { executor.execute(job); }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void registerShutdownHook() { Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown)); }
}
