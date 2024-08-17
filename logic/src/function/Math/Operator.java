package function.Math;

import function.Function;

import java.util.List;

import static common.utils.InputValidation.validateOrThrow;
import static java.lang.Double.NaN;

public abstract class Operator extends Function<Double> {

    @Override
    public final Double execute(List<Object> args) {
        try {
            List<Double> operands = parse(args);
            return calc(operands);
        } catch (Exception e) {
            return NaN;
        }
    }

    protected abstract Double calc(List<Double> operands);

    protected boolean isValidSecondOperand(Double secondOperand) {
        return true;
    }

    protected void validateSecondOperand(List<Double> operands) {
        validateOrThrow(
                operands,
                ops -> ops != null && (ops.size() != 2 || isValidSecondOperand(ops.getLast())),
                error -> this.getClass().getSimpleName() + " by zero"
        );
    }

    protected List<Double> argsToDoubleArray(List<Object> args) {
        return super.argsToTypeArray(Double.class, args);
    }

    private List<Double> parse(List<Object> args) {
        checkNumberOfArgs(args);
        List<Double> operands = argsToDoubleArray(args);
        validateSecondOperand(operands);
        return operands;
    }
}
