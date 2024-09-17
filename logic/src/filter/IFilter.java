package filter;

import cell.Cell;
import range.CellRange;

import java.util.List;
import java.util.Map;

public interface IFilter {
    List<Cell> byRange(CellRange range);
    List<Integer> ByValues(CellRange range, Map<Character, String> selectedValues);
    List<Integer> byMultiColumns(CellRange range, List<Map<Character, String>> selectedValues, boolean isAnd);
    List<Cell> getCellsByRows(CellRange range, List<Integer> rows);
}
