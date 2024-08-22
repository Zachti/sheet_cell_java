package engine;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import filter.dto.MultiColumnsFilterConfig;
import position.interfaces.IPosition;
import range.CellRange;
import filter.dto.FilterConfig;
import sheet.dto.SortConfig;

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
    List<Integer> getRowsByFilter(FilterConfig filterConfig);
    List<Integer> sortRowsInRange(SortConfig sortConfig);
    Map<IPosition, Cell> getWhatIfCells(List<UpdateCellDto> updateCellDtos);
    List<Integer> getRowsByMultiColumnsFilter(MultiColumnsFilterConfig filterConfig);
}
