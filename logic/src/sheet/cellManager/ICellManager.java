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
    Map<IPosition, Cell> getWhatIfCells(List<UpdateCellDto> updateCellDtos);
    Map<IPosition, Cell> getCellsByFilter(IRange range, Map<Character, String> selectedValues);
    List<Integer> getRowsByMultiColumnsFilter(IRange range, List<Map<Character, String>> selectedValues, boolean isAnd);
    List<Cell> getCellsInRange(IRange range);
}
