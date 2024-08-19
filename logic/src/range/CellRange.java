package range;

import cell.Cell;
import position.interfaces.IPosition;

import java.util.LinkedList;
import java.util.List;

public record CellRange(IPosition start, IPosition end) {
    public boolean contains(IPosition position) {
        return position.column() >= start.column() && position.column() <= end.column() &&
                position.row() >= start.row() && position.row() <= end.row();
    }

    public List<Cell> sortRangeBy(List<Cell> cells, List<Character> columns) {
// todo - implement
        return new LinkedList<>();
    }

    public List<Integer> sortCellsByColumn(List<Cell> cells) {
        cells.sort((c1, c2) -> {
            Comparable<String> value1 = c1.getEffectiveValue();
            String value2 = c2.getEffectiveValue();
            return value1.compareTo(value2);
        });
        return cells.stream().map(cell -> cell.getPosition().row()).toList();
    }
}
