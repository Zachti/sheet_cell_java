package function.Math;

import function.enums.NumberOfArgs;

import java.util.List;

public final class Abs extends Operator {

    @Override
    protected Double calc(List<Double> operands) {
        return Math.abs(operands.getFirst());
    }

    @Override
    protected int getNumberOfArgs() {
        return NumberOfArgs.ONE.toInt();
    }

    @Override
    protected List<Double> argsToDoubleArray(List<Object> args) {
        List<Double> operands = argsToTypeArray(Double.class, args);
        operands.addLast(0.0);
        return operands;
    }
}
