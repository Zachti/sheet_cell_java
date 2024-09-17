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
    public List<Integer> ByValues(CellRange range, Map<Character, String> selectedValues) {
        return byRange(range).stream()
                .filter(cell -> isCellInSelectedValues(cell, selectedValues))
                .map(cell -> cell.getPosition().row())
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> byMultiColumns(CellRange range, List<Map<Character, String>> selectedValues, boolean isAnd) {
        return isAnd ? ByAndValues(range, selectedValues) : ByOrValues(range, selectedValues);
    }

    private List<Integer> ByAndValues(CellRange range, List<Map<Character, String>> selectedValues) {
        return selectedValues.stream()
                .map(values -> ByValues(range, values))
                .reduce((rows1, rows2) -> rows1.stream().filter(rows2::contains).collect(Collectors.toList()))
                .orElse(Collections.emptyList());

    }

    private List<Integer> ByOrValues(CellRange range, List<Map<Character, String>> selectedValues) {
        return selectedValues.stream()
                .map(values -> ByValues(range, values))
                .reduce((rows1, rows2) -> Stream.concat(rows1.stream(), rows2.stream()).distinct().collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Cell> getCellsByRows(CellRange range, List<Integer> rows) {
        return byRange(range).stream().filter(cell -> rows.contains(cell.getPosition().row())).toList();
    }

    private boolean isCellInSelectedValues(Cell cell, Map<Character, String> selectedValues) {
        return selectedValues.containsKey(cell.getPosition().column())
                && selectedValues.get(cell.getPosition().column()).equals(cell.getEffectiveValue());
    }
}
