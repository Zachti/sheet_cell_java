package filter;

import cell.Cell;
import position.interfaces.IPosition;
import range.IRange;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public Map<IPosition, Cell> ByValues(IRange range, List<String> selectedValues) {
        List<Integer> rows = byRange(range).stream()
                .filter(cell -> selectedValues.contains(cell.getEffectiveValue()))
                .map(cell -> cell.getPosition().row())
                .toList();
        return rowsToCellMap(range, rows);
    }

    @Override
    public Map<IPosition, Cell> byMultiColumns(IRange range, Map<Character,  List<String>> selectedValues, boolean isAnd) {
        return isAnd ? ByAndValues(range, selectedValues) : ByOrValues(range, selectedValues);
    }

    private Map<IPosition, Cell> ByAndValues(IRange range, Map<Character,  List<String>> selectedValues) {
        List<Integer> rows = getRowsInFilter(range, selectedValues).stream()
                .filter(row -> containsAtLeastOneFromEachCol(row, selectedValues)).toList();
        return rowsToCellMap(range, rows);

    }

    private Map<IPosition, Cell> ByOrValues(IRange range, Map<Character,  List<String>> selectedValues) {
            List<Integer> rows = getRowsInFilter(range, selectedValues).stream().distinct().toList();
            return rowsToCellMap(range, rows);
    }

    private List<Cell> getCellsByRows(IRange range, List<Integer> rows) {
        return byRange(range).stream().filter(cell -> rows.contains(cell.getPosition().row())).toList();
    }

    private List<String> rowIndexToValueList(int row) {
        List<String> rowValues = new ArrayList<>();
        position2Cell.forEach((key, value) -> {
            if (key.row() == row) {
                rowValues.add(value.getEffectiveValue());
            }
        });
        return rowValues;
    }

    private boolean containsAtLeastOneFromEachCol(Integer row, Map<Character, List<String>> selectedValues) {
        return selectedValues.entrySet().stream()
                .allMatch(entry ->
                        rowIndexToValueList(row).stream()
                                .anyMatch(value -> entry.getValue().contains(value)));
    }

    private Map<IPosition, Cell> rowsToCellMap(IRange range, List<Integer> rows) {
        return getCellsByRows(range, rows).stream()
                .collect(Collectors.toMap(Cell::getPosition, Function.identity()));
    }

    private List<Integer> getRowsInFilter(IRange range, Map<Character,  List<String>> selectedValues) {
        return selectedValues.values().stream()
                .flatMap(values -> ByValues(range, values).keySet().stream())
                .map(IPosition::row)
                .distinct().toList();
    }
}
