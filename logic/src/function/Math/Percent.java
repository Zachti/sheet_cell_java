package function.Math;

import java.util.List;

public class Percent extends Operator {

    @Override
    protected Double calc(List<Double> operands) {
        return operands.get(0) * operands.get(1) / 100;
    }
}
