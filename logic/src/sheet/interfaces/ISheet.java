package sheet.interfaces;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import position.interfaces.IPosition;
import range.CellRange;

import java.util.List;
import java.util.Map;

public interface ISheet extends Cloneable {
    void updateCell(UpdateCellDto updateCellDto);
    Map<IPosition, Cell> getPastVersion(int version);
    Cell getCellByPosition(IPosition position);
    String getName();
    void validatePositionOnSheet(IPosition position);
    Map<Integer, Integer> getUpdate2VersionCount();
    CellBasicDetails getCellBasicDetails(IPosition position);
    CellDetails getCellDetails(IPosition position);
    Map<IPosition, Cell> getCells();
    int getVersion();
    void addRange(CellRange range);
    List<CellRange> getRanges();
    void removeRange(CellRange range);
    List<Cell> viewCellsInRange(CellRange range);
    List<Integer> getRowsByFilter(CellRange range, List<Object> selectedValues);
    List<Integer> sortRowsInRange(CellRange range, List<Character> columns, boolean ascending);
    Map<IPosition, Cell> getWhatIfCells(List<UpdateCellDto> updateCellDtos);
    List<Integer> getRowsByMultiColumnsFilter(CellRange range, List<List<Object>> selectedValues, boolean isAnd);
}

