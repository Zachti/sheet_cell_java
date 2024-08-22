package sheet.cellManager;

import cell.Cell;
import position.interfaces.IPosition;

import java.util.List;
import java.util.Map;

public interface ICellManager {
    Cell update(IPosition position, String value, int version);
    int initializeCells(List<Cell> cells);
    Map<IPosition, Cell> computePastVersion(int version);
    Cell getCellByPosition(IPosition position);
    void validatePositionOnSheet(IPosition position);
    Map<IPosition, Cell> getCells();
    Map<IPosition, Cell> getWhatIfCells(String originalValue, IPosition position);
}
