package function;

import function.enums.NumberOfArgs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static common.utils.InputValidation.validateOrThrow;

public abstract class Function<T> implements IFunction {

    @Override
    public abstract T execute(List<Object> args);

    protected int getNumberOfArgs() {
        return  NumberOfArgs.TWO.toInt();
    }

    protected List<T> argsToTypeArray(Class<T> type, List<Object> args) {
        return args.stream()
                .map(type::cast)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    protected void checkNumberOfArgs(List<Object> args) {
        validateOrThrow(
                args,
                a -> a.size() == getNumberOfArgs() || getNumberOfArgs() == Integer.MAX_VALUE,
                _ -> "Function requires exactly " + getNumberOfArgs() + " arguments"
        );
    }
}
