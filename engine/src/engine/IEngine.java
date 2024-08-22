package engine;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import position.interfaces.IPosition;
import range.CellRange;

import java.util.List;
import java.util.Map;

public interface IEngine {
    Map<IPosition, Cell> getHistory(int version);
    String getSheetName();
    Map<Integer, Integer> getUpdateCountList();
    void updateCell(UpdateCellDto updateCellDto);
    CellBasicDetails getCellBasicDetails(IPosition position);
    CellDetails getCellDetails(IPosition position);
    void addRange(CellRange range);
    void removeRange(CellRange range);
    List<CellRange> getRanges();
    List<Cell> viewCellsInRange(CellRange range);
    List<Integer> getRowsByFilter(CellRange range, List<Object> selectedValues);
    List<Integer> sortRowsInRange(CellRange range, List<Character> columns, boolean ascending);
}
