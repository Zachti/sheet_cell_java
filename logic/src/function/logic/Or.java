package function.logic;

import function.enums.NumberOfArgs;

import java.util.List;

public class Or extends Predicate {

    @Override
    protected Boolean applyCondition(List<Object> args) {
        List<Boolean> booleans = argsToBooleanArray(args);
        return booleans.stream().reduce(false, (a, b) -> a || b);
    }
}
