package function.range;

import function.Function;
import function.enums.NumberOfArgs;
import range.CellRange;
import store.TypedContextStore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static common.utils.ValueParser.isNumeric;
import static java.lang.Double.NaN;


public abstract class Aggregator extends Function<Double> {

    @Override
    public final Double execute(List<Object> args) {
        try {
            List<Double> values = rangeToValueList(args);
            return emptyOrCalculate(values);
        } catch (Exception e) {
            return NaN;
        }
    }

    protected abstract Double calculate(List<Double> args);

    private Double emptyOrCalculate(List<Double> args) {
        return args.isEmpty() ? 0 : calculate(args);

    }

    @Override
    protected int getNumberOfArgs() { return NumberOfArgs.ONE.toInt(); }

    private List<Double> rangeToValueList(List<Object> args) {
        List<Double> cellValues = new LinkedList<>();
        checkNumberOfArgs(args);
        CellRange range = argsToCellRange(args);
        TypedContextStore.getSheetStore().getContext().getCells().forEach((pos, cell) -> {
            String value = cell.getEffectiveValue().replace(",", "");
            if (range.contains(pos) && isNumeric(value)) {
                cellValues.add(Double.parseDouble(value));
            }
        });
        return cellValues;
        // todo - do not forget to set context !
    }

    protected CellRange argsToCellRange(List<Object> args) {
        return args.stream()
                .map(CellRange.class::cast)
                .collect(Collectors.toCollection(ArrayList::new))
                .getFirst();
    }
}
