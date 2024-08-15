package sheet.interfaces;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import position.interfaces.IPosition;

import java.util.Map;

public interface ISheet extends Cloneable {
    void updateCell(IPosition position, Object value);
    Map<IPosition, Cell> getPastVersion(int version);
    Cell getCellByPosition(IPosition position);
    String getName();
    void validatePositionOnSheet(IPosition position);
    Map<Integer, Integer> getUpdate2VersionCount();
    CellBasicDetails getCellBasicDetails(IPosition position);
    CellDetails getCellDetails(IPosition position);
}
