package filter;

import cell.Cell;
import range.CellRange;

import java.util.List;

public interface IFilter {
    List<Cell> byRange(CellRange range);
    List<Integer> ByValues(CellRange range, List<Object> selectedValues);
    List<Integer> byMultiColumns(CellRange range, List<List<Object>> selectedValue, boolean isAnd);
}
