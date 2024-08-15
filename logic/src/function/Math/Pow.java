package function.Math;

import java.util.List;

public final class Pow extends Operator {
    @Override
    protected Double calc(List<Double> operands) {
        return Math.pow(operands.getFirst(), operands.getLast());
    }
}
