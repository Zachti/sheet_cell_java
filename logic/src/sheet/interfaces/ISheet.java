package sheet.interfaces;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import position.interfaces.IPosition;
import range.IRange;
import sheet.SheetHistory;

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
    List<Cell> viewCellsInRange(IRange range);
    Map<IPosition, Cell> getCellsByFilter(IRange range, Map<Character, List<String>> selectedValues);
    List<Integer> sortRowsInRange(IRange range, List<Character> columns, boolean ascending);
    Map<IPosition, Cell> getWhatIfCells(List<UpdateCellDto> updateCellDtos);
    List<Integer> getRowsByMultiColumnsFilter(IRange range, List<Map<Character, List<String>>> selectedValues, boolean isAnd);
}

