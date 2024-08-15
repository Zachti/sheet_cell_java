package sheet.manager;

import cell.Cell;
import position.interfaces.IPosition;

import java.util.List;
import java.util.Map;

public interface ICellManager {
    Cell update(IPosition position, Object value, int version);
    int initializeCells(List<Cell> cells);
    Map<IPosition, Cell> computePastVersion(int version);
    Cell getCellByPosition(IPosition position);
    void validatePositionOnSheet(IPosition position);
    Map<IPosition, Cell> getCells();
}
