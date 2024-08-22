package common.interfaces;

import java.util.concurrent.ExecutionException;

@FunctionalInterface
public interface IHandler {
    void handle() throws ExecutionException, InterruptedException;
}
