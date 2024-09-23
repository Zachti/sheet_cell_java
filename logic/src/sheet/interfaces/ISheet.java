package sheet.interfaces;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import position.interfaces.IPosition;
import range.IRange;
import sheet.history.SheetHistory;

import java.util.List;
import java.util.Map;

public interface ISheet extends Cloneable {
    void updateCell(UpdateCellDto updateCellDto);
    SheetHistory getPastVersion(int version);
    Cell getCellByPosition(IPosition position);
    String getName();
    void validatePositionOnSheet(IPosition position);
    Map<Integer, Integer> getUpdate2VersionCount();
    CellBasicDetails getCellBasicDetails(IPosition position);
    CellDetails getCellDetails(IPosition position);
    Map<IPosition, Cell> getCells();
    int getVersion();
    void addRange(IRange range);
    List<IRange> getRanges();
    void removeRangeOrThrow(String rangeName);
    Map<IPosition, Cell> viewCellsInRange(IRange range);
    Map<IPosition, Cell> getCellsByFilter(IRange range, List<String> selectedValues);
    List<Integer> sortRowsInRange(IRange range, List<Character> columns, boolean ascending);
    Map<IPosition, Cell> getWhatIfCells(UpdateCellDto updateCellDtos);
    Map<IPosition, Cell> getRowsByMultiColumnsFilter(IRange range, Map<Character, List<String>> selectedValues, boolean isAnd);
}

