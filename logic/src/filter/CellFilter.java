package filter;

import cell.Cell;
import position.interfaces.IPosition;
import range.IRange;

import java.util.*;
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
    public List<Integer> ByValues(IRange range, List<String> selectedValues) {
        return byRange(range).stream()
                .filter(cell -> selectedValues.contains(cell.getEffectiveValue()))
                .map(cell -> cell.getPosition().row())
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> byMultiColumns(IRange range, Map<Character,  List<String>> selectedValues, boolean isAnd) {
        return isAnd ? ByAndValues(range, selectedValues) : ByOrValues(range, selectedValues);
    }

    private List<Integer> ByAndValues(IRange range, Map<Character,  List<String>> selectedValues) {
        List<Integer> rows = selectedValues.values().stream()
                .flatMap(values -> ByValues(range, values).stream()).toList();
        return rows.stream().map(this::rowIndexToValueList)
                .filter(rowValues -> containsAtLeastOneFromEachCol(rowValues, selectedValues))
                .flatMap(rowValues -> rowValues.keySet().stream()).toList();
    }

    private List<Integer> ByOrValues(IRange range, Map<Character,  List<String>> selectedValues) {
        return selectedValues.values().stream()
                .map(values -> ByValues(range, values))
                .reduce((rows1, rows2) -> Stream.concat(rows1.stream(), rows2.stream()).distinct().collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Cell> getCellsByRows(IRange range, List<Integer> rows) {
        return byRange(range).stream().filter(cell -> rows.contains(cell.getPosition().row())).toList();
    }

    private Map<Integer, String> rowIndexToValueList(int row) {
        Map<Integer, String> rowValues = new HashMap<>();
        position2Cell.forEach((key, value) -> {
            if (key.row() == row) {
                rowValues.put(row, value.getEffectiveValue());
            }
        });
        return rowValues;
    }

    private boolean containsAtLeastOneFromEachCol(Map<Integer, String> rowValues, Map<Character, List<String>> selectedValues) {
        return selectedValues.entrySet().stream()
                .allMatch(entry ->
                        rowValues.values().stream()
                                .anyMatch(value -> entry.getValue().contains(value)));
    }
}
