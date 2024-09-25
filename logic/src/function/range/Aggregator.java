package function.range;

import cell.Cell;
import function.Function;
import function.enums.NumberOfArgs;
import position.interfaces.IPosition;
import range.IRange;
import sheet.interfaces.ISheet;
import store.SetContextStore;
import store.TypedContextStore;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        getSheetCellsList(sheet).forEach((pos, cell) -> {
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
        return (ranges != null && ranges.containsKey(stringArg))
                ? ranges.get(stringArg)
                : sheet.getRanges().stream()
                .filter(range -> range.getName().equals(stringArg))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Range not found: " + stringArg));
    }

    private void onCellInRange(Cell callingCell, Cell inRangeCell, List<Double> cellValues, IRange range, String value) {
        inRangeCell.addObserver(callingCell);
        callingCell.addObservable(inRangeCell);
        cellValues.add(Double.parseDouble(value));
        range.addUser(callingCell);
    }

    private Map<IPosition, Cell> getSheetCellsList(ISheet sheet) {
        return Optional.ofNullable(sheet)
                .map(ISheet::getCells)
                .orElseGet(() -> SetContextStore.getCellSetStore()
                        .getContext().stream().collect(Collectors.toMap(Cell::getPosition, cell -> cell)));
    }
}
