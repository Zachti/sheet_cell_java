package cell.dto;

import position.PositionFactory;
import position.interfaces.IPosition;

public record CreateCellDto(String originalValue, int row, char column) {

    public IPosition getPosition() { return PositionFactory.create(row, column); }
}
