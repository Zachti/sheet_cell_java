package position;

import position.interfaces.IPosition;

public record Position(int row, char column) implements IPosition {

    @Override
    public String toString() {
        return String.format("%s%s", column , row);
    }
}
