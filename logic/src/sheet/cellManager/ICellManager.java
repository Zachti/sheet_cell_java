package sheet.cellManager;

import cell.Cell;
import cell.dto.UpdateCellDto;
import position.interfaces.IPosition;
import range.IRange;

import java.util.List;
import java.util.Map;

public interface ICellManager {
    Cell update(UpdateCellDto updateCellDto, int version);
    int initializeCells(List<Cell> cells);
    Map<IPosition, Cell> computePastVersion(int version);
    Cell getCellByPosition(IPosition position);
    void validatePositionOnSheet(IPosition position);
    Map<IPosition, Cell> getCells();
    List<Integer> sortRowsInRange(IRange range, List<Character> columns, boolean ascending);
    Map<IPosition, Cell> getWhatIfCells(UpdateCellDto updateCellDto);
    Map<IPosition, Cell> getCellsByFilter(IRange range, List<String> selectedValues);
    Map<IPosition, Cell> getRowsByMultiColumnsFilter(IRange range, Map<Character, List<String>> selectedValues, boolean isAnd);
    List<Cell> getCellsInRange(IRange range);
}
