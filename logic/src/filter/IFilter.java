package filter;

import cell.Cell;
import range.IRange;

import java.util.List;
import java.util.Map;

public interface IFilter {
    List<Cell> byRange(IRange range);
    List<Integer> ByValues(IRange range, List<String> selectedValues);
    List<Integer> byMultiColumns(IRange range, Map<Character, List<String>> selectedValues, boolean isAnd);
    List<Cell> getCellsByRows(IRange range, List<Integer> rows);
}
