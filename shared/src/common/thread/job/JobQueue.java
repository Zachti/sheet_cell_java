package common.thread.job;

import common.thread.job.interfaces.IJobQueue;

import java.util.concurrent.*;

public class JobQueue implements IJobQueue {
    private final ThreadPoolExecutor executor;

    public JobQueue(int poolSize, int queueSize) {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueSize);
        executor = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, queue);
        registerShutdownHook();
    }

    @Override
    public <T> Future<T> addJob(Callable<T> job) { return executor.submit(job); }

    @Override
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
