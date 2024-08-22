package engine;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import position.interfaces.IPosition;

import java.util.Map;

public interface IEngine {
    Map<IPosition, Cell> getHistory(int version);
    String getSheetName();
    Map<Integer, Integer> getUpdateCountList();
    void updateCell(UpdateCellDto updateCellDto);
    CellBasicDetails getCellBasicDetails(IPosition position);
    CellDetails getCellDetails(IPosition position);
    Map<IPosition, Cell> getWhatIfCells(String originalValue, IPosition position);
}
