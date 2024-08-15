package common.interfaces;

@FunctionalInterface
public interface IGenericHandler<T> {
    T handle();
}


