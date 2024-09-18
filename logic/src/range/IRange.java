package range;

import cell.Cell;
import position.interfaces.IPosition;

import java.util.Map;

public interface IRange {
    String getName();
    IPosition getStart();
    IPosition getEnd();
    boolean contains(IPosition position);
    boolean isValidToDelete();
    void addUser(Cell cell);
    void removeUser(IPosition pos);
    Map<IPosition, Cell> getUsers();
    IRange clone();
}
