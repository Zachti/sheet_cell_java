package function.Math;

import java.util.List;

public final class Minus extends Operator {
    @Override
    protected Double calc(List<Double> operands) {
        return operands.getFirst() - operands.getLast();
    }
}
