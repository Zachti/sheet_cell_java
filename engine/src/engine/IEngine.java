package engine;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import filter.dto.MultiColumnsFilterConfig;
import position.interfaces.IPosition;
import range.CellRange;
import filter.dto.FilterConfig;
import range.IRange;
import sheet.dto.SortConfig;
import sheet.history.SheetHistory;

import java.util.List;
import java.util.Map;

public interface IEngine {
    SheetHistory getHistory(int version);
    String getSheetName();
    Map<Integer, Integer> getUpdateCountList();
    void updateCell(UpdateCellDto updateCellDto);
    CellBasicDetails getCellBasicDetails(IPosition position);
    CellDetails getCellDetails(IPosition position);
    void addRange(CellRange range);
    void removeRange(String rangeName);
    List<IRange> getRanges();
    List<Cell> viewCellsInRange(CellRange range);
    Map<IPosition, Cell> getCellsByFilter(FilterConfig filterConfig);
    List<Integer> sortRowsInRange(SortConfig sortConfig);
    Map<IPosition, Cell> getWhatIfCells(List<UpdateCellDto> updateCellDtos);
    Map<IPosition, Cell> getRowsByMultiColumnsFilter(MultiColumnsFilterConfig filterConfig);
}
