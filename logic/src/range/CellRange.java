package range;

import position.interfaces.IPosition;

public record CellRange(IPosition start, IPosition end) {
    public boolean contains(IPosition position) {
        return position.column() >= start.column() && position.column() <= end.column() &&
                position.row() >= start.row() && position.row() <= end.row();
    }
}
