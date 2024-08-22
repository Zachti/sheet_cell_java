package function.logic;

import function.enums.NumberOfArgs;

import java.util.List;

public class Not extends Predicate {

    @Override
    protected Boolean applyCondition(List<Object> args) {
        List<Boolean> booleans = argsToBooleanArray(args);
        return !booleans.getFirst();
    }

    @Override
    protected int getNumberOfArgs() { return NumberOfArgs.ONE.toInt(); }
}
