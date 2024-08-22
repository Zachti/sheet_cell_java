package function.logic;

import java.util.List;

public class And extends Predicate {

        @Override
        protected Boolean applyCondition(List<Object> args) {
            List<Boolean> booleans = argsToBooleanArray(args);
            return booleans.stream().reduce(true, (a, b) -> a && b);
        }
}
