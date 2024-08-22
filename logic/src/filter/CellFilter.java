package filter;

import cell.Cell;
import position.interfaces.IPosition;
import range.CellRange;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CellFilter implements IFilter {
    private final Map<IPosition, Cell> position2Cell;

    public CellFilter(Map<IPosition, Cell> position2Cell) {
        this.position2Cell = position2Cell;
    }

    @Override
    public List<Cell> byRange(CellRange range) {
        return position2Cell.entrySet().stream()
                .filter(entry -> range.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> ByValues(CellRange range, List<Object> selectedValues) {
        return byRange(range).stream()
                .filter(cell -> selectedValues.contains(cell.getEffectiveValue()))
                .map(cell -> cell.getPosition().row())
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> byMultiColumns(CellRange range, List<List<Object>> selectedValue, boolean isAnd) {
        return isAnd ? ByAndValues(range, selectedValue) : ByOrValues(range, selectedValue);
    }

    private List<Integer> ByAndValues(CellRange range, List<List<Object>> selectedValue) {
        return selectedValue.stream()
                .map(selectedValues -> ByValues(range, selectedValues))
                .reduce((rows1, rows2) -> rows1.stream().filter(rows2::contains).collect(Collectors.toList()))
                .orElse(Collections.emptyList());

    }

    private List<Integer> ByOrValues(CellRange range, List<List<Object>> selectedValue) {
        return selectedValue.stream()
                .map(selectedValues -> ByValues(range, selectedValues))
                .reduce((rows1, rows2) -> Stream.concat(rows1.stream(), rows2.stream()).distinct().collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

}
