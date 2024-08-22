package engine;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import filter.dto.FilterConfig;
import filter.dto.MultiColumnsFilterConfig;
import position.interfaces.IPosition;
import range.CellRange;
import sheet.dto.SortConfig;
import sheet.interfaces.ISheet;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

public interface IEngine {
    Future<Map<IPosition, Cell>> getHistory(int version, UUID id);
    Future<String> getSheetName(UUID id);
    Future<Map<Integer, Integer>> getUpdateCountList(UUID id);
    Future<Void> updateCell(UpdateCellDto updateCellDto, UUID id);
    Future<CellBasicDetails> getCellBasicDetails(IPosition position, UUID id);
    Future<CellDetails> getCellDetails(IPosition position, UUID id);
    Future<Void> addRange(CellRange range, UUID id);
    Future<Void> removeRange(CellRange range, UUID id);
    Future<List<CellRange>> getRanges(UUID id);
    Future<List<Cell>> viewCellsInRange(CellRange range, UUID id);
    Future<List<Integer>> getRowsByFilter(FilterConfig filterConfig, UUID id);
    Future<List<Integer>> sortRowsInRange(SortConfig sortConfig, UUID id);
    Future<Map<IPosition, Cell>> getWhatIfCells(List<UpdateCellDto> updateCellDtos, UUID id);
    Future<List<Integer>> getRowsByMultiColumnsFilter(MultiColumnsFilterConfig filterConfig, UUID id);
    UUID addSheet(ISheet sheet);
    void removeSheet(UUID id);
}
