package sheet.cellManager;

import cell.Cell;
import cell.dto.UpdateCellDto;
import position.interfaces.IPosition;
import range.CellRange;

import java.util.List;
import java.util.Map;

public interface ICellManager {
    Cell update(UpdateCellDto updateCellDto, int version);
    int initializeCells(List<Cell> cells);
    Map<IPosition, Cell> computePastVersion(int version);
    Cell getCellByPosition(IPosition position);
    void validatePositionOnSheet(IPosition position);
    Map<IPosition, Cell> getCells();
    List<Integer> sortRowsInRange(CellRange range, List<Character> columns, boolean ascending);
    Map<IPosition, Cell> getWhatIfCells(List<UpdateCellDto> updateCellDtos);
    List<Integer> getRowsByFilter(CellRange range, List<Object> selectedValues);
    List<Integer> getRowsByMultiColumnsFilter(CellRange range, List<List<Object>> selectedValues, boolean isAnd);
    List<Cell> getCellsInRange(CellRange range);
}
