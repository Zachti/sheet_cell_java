package function.logic;

import function.Function;
import function.enums.NumberOfArgs;

import java.util.List;

public class If extends Function<Object> {

    @Override
    public Object execute(List<Object> args) {
        checkNumberOfArgs(args);
        return applyCondition(args);
    }

    private Object applyCondition(List<Object> args) {
        boolean condition = (Boolean) args.getFirst();
        Object then = args.get(1);
        Object elze = args.getLast();
        validateReturnType(then, elze);
        return condition ? then : elze;
    }

    @Override
    protected int getNumberOfArgs() { return NumberOfArgs.THREE.toInt(); }

    private void validateReturnType(Object then, Object elze) {
        if (!then.getClass().equals(elze.getClass())) {
            throw new IllegalArgumentException("return values must be from the same type!");
        }
    }

}
