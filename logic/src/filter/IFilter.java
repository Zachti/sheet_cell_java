package filter;

import cell.Cell;
import position.interfaces.IPosition;
import range.IRange;

import java.util.List;
import java.util.Map;

public interface IFilter {
    List<Cell> byRange(IRange range);
    Map<IPosition, Cell> ByValues(IRange range, List<String> selectedValues);
    Map<IPosition, Cell> byMultiColumns(IRange range, Map<Character, List<String>> selectedValues, boolean isAnd);
}
