package function.logic;

import function.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Predicate extends Function<Boolean> {

    @Override
    public final Boolean execute(List<Object> args) {
        checkNumberOfArgs(args);
        return applyCondition(args);
    }

    protected abstract Boolean applyCondition(List<Object> args);

    protected List<Boolean> argsToBooleanArray(List<Object> args) { return super.argsToTypeArray(Boolean.class, args); }

    protected List<Double> argsToDoubleList(List<Object> args) {
        return args.stream()
                .map(Double.class::cast)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
