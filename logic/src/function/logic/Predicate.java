package function.logic;

import function.Function;

import java.util.List;

public abstract class Predicate extends Function<Boolean> {

    @Override
    public final Boolean execute(List<Object> args) {
        return applyCondition(args);
    }

    protected abstract Boolean applyCondition(Object... args);
}
