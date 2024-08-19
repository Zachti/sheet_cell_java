package function.range;


import java.util.List;

public class Sum extends Aggregator {

    @Override
    protected Double calculate(List<Double> values) {
        return values.stream().reduce(0.0, Double::sum);
    }

}
