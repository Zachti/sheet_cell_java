package range;

import cell.Cell;
import position.interfaces.IPosition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CellRange implements IRange{
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

    @Override
    public String getName() { return name; }

    @Override
    public IPosition getStart() { return start; }

    @Override
    public IPosition getEnd() { return end; }

    @Override
    public Map<IPosition, Cell> getUsers() { return new HashMap<>(users); }

    @Override
    public void addUser(Cell cell) { users.put(cell.getPosition(), cell); }

    @Override
    public void removeUser(IPosition pos) {
        Optional.ofNullable(users.get(pos)).ifPresent(cell -> users.remove(pos));
    }

    @Override
    public boolean isValidToDelete() { return users.isEmpty(); }

    @Override
    public boolean contains(IPosition position) {
        return position.column() >= start.column() && position.column() <= end.column() &&
                position.row() >= start.row() && position.row() <= end.row();
    }

    public static CellRange of(String name, IPosition start, IPosition end) {
        return new CellRange(name, start, end, new HashMap<>());
    }
}

