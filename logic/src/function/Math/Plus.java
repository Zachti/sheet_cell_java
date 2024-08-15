package function.Math;

import java.util.List;

public final class Plus extends Operator {
    @Override
    protected Double calc(List<Double> operands) {
        return operands.getFirst() + operands.getLast();
    }
}
