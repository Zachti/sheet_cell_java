package filter;

import cell.Cell;
import position.interfaces.IPosition;
import range.IRange;

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
    public List<Cell> byRange(IRange range) {
        return position2Cell.entrySet().stream()
                .filter(entry -> range.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> ByValues(IRange range, Map<Character, List<String>> selectedValues) {
        return byRange(range).stream()
                .filter(cell -> isCellInSelectedValues(cell, selectedValues))
                .map(cell -> cell.getPosition().row())
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> byMultiColumns(IRange range, List<Map<Character,  List<String>>> selectedValues, boolean isAnd) {
        return isAnd ? ByAndValues(range, selectedValues) : ByOrValues(range, selectedValues);
    }

    private List<Integer> ByAndValues(IRange range, List<Map<Character,  List<String>>> selectedValues) {
        return selectedValues.stream()
                .map(values -> ByValues(range, values))
                .reduce((rows1, rows2) -> rows1.stream().filter(rows2::contains).collect(Collectors.toList()))
                .orElse(Collections.emptyList());

    }

    private List<Integer> ByOrValues(IRange range, List<Map<Character,  List<String>>> selectedValues) {
        return selectedValues.stream()
                .map(values -> ByValues(range, values))
                .reduce((rows1, rows2) -> Stream.concat(rows1.stream(), rows2.stream()).distinct().collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Cell> getCellsByRows(IRange range, List<Integer> rows) {
        return byRange(range).stream().filter(cell -> rows.contains(cell.getPosition().row())).toList();
    }

    private boolean isCellInSelectedValues(Cell cell, Map<Character,  List<String>> selectedValues) {
        return selectedValues.containsKey(cell.getPosition().column())
                && selectedValues.get(cell.getPosition().column()).stream()
                .anyMatch(value -> value.equals(cell.getEffectiveValue()));
    }
}
