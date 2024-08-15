package function.Math;

import java.util.List;

public final class Divide extends Operator {
    @Override
    protected Double calc(List<Double> operands) {
        return operands.getFirst() / operands.getLast();
    }

    @Override
    protected boolean isValidSecondOperand(Double secondOperand) { return secondOperand != 0; }
}
