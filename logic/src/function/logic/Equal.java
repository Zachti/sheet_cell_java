package function.logic;

import java.util.List;

public class Equal extends Predicate {

    @Override
    protected Boolean applyCondition(List<Object> args) {
        Object value1 = args.get(0);
        Object value2 = args.get(1);
        if (value1 instanceof Number && value2 instanceof Number) {
            return Double.compare(((Number) value1).doubleValue(), ((Number) value2).doubleValue()) == 0;
        }

        return value1.equals(value2);
    }
}
