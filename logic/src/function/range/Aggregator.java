package function.range;

import cell.Cell;
import cell.observability.interfaces.ISubject;
import function.Function;
import function.enums.NumberOfArgs;
import range.IRange;
import sheet.interfaces.ISheet;
import store.TypedContextStore;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static common.utils.ValueParser.isNumeric;
import static java.lang.Double.NaN;


public abstract class Aggregator extends Function<Double> {

    @Override
    public final Double execute(List<Object> args) {
        try {
            checkNumberOfArgs(args);
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
        ISheet sheet = TypedContextStore.getSheetStore().getContext();
        IRange range = argsToIRange(args.getFirst(), sheet);
        Cell callingCell = (Cell) TypedContextStore.getSubjectStore().getContext();
        sheet.getCells().forEach((pos, cell) -> {
            String value = cell.getEffectiveValue().replace(",", "");
            if (range.contains(pos) && isNumeric(value)) {
                this.onCellInRange(callingCell, cell, cellValues, range, value);
            }
        });
        return cellValues;
    }

    protected IRange argsToIRange(Object arg, ISheet sheet) {
        Map<String, IRange> ranges = TypedContextStore.getRangesStore().getContext();
        String stringArg = arg.toString();
        if (ranges != null && ranges.containsKey(stringArg)) {
            return ranges.get(stringArg);
        }
        return sheet.getRanges().stream()
                .filter(range -> range.getName().equals(stringArg))
                .findFirst()
                .orElseThrow();
    }

    private void onCellInRange(Cell callingCell, Cell inRangeCell, List<Double> cellValues, IRange range, String value) {
        inRangeCell.addObserver(callingCell);
        callingCell.addObservable(inRangeCell);
        cellValues.add(Double.parseDouble(value));
        range.addUser(callingCell);
    }
}
