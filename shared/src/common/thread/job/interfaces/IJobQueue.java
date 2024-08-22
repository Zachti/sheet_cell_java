package common.thread.job.interfaces;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface IJobQueue {
    <T> Future<T> addJob(Callable<T> job);
    void shutdown();
}
