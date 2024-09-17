package range;

import cell.Cell;
import position.interfaces.IPosition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CellRange {
    private final String name;
    private final IPosition start;
    private final IPosition end;
    private final Map<IPosition, Cell> users;

    private CellRange(String name, IPosition start, IPosition end, Map<IPosition, Cell> users) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.users = new HashMap<>(users);
    }

    public String getName() { return name; }

    public IPosition getStart() { return start; }

    public IPosition getEnd() { return end; }

    public Map<IPosition, Cell> getUsers() { return new HashMap<>(users); }

    public void addUser(Cell cell) { users.put(cell.getPosition(), cell); }

    public void removeUser(IPosition pos) {
        Optional.ofNullable(users.get(pos)).ifPresent(cell -> users.remove(pos));
    }

    public boolean isValidToDelete() { return users.isEmpty(); }

    public boolean contains(IPosition position) {
        return position.column() >= start.column() && position.column() <= end.column() &&
                position.row() >= start.row() && position.row() <= end.row();
    }

    public static CellRange of(String name, IPosition start, IPosition end) {
        return new CellRange(name, start, end, new HashMap<>());
    }
}

