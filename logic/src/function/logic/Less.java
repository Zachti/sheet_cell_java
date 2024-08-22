package function.logic;

import java.util.List;

public class Less extends Predicate {

        @Override
        protected Boolean applyCondition(List<Object> args) {
            List<Double> operands = argsToDoubleList(args);
            return operands.getFirst() <= operands.getLast();
        }
}
