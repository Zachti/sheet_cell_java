package position;

import position.interfaces.IPosition;

import static common.utils.InputValidation.validateOrThrow;

public record Position(int row, char column) implements IPosition {

    @Override
    public String toString() {
        return String.format("%s%s", column , row);
    }

    public static Position fromString(String s) {
       validateOrThrow(
                s,
                str -> str != null && str.length() == 2,
                str -> "Invalid input string for Position: " + str
        );

        return PositionFactory.create(Character.getNumericValue(s.charAt(1)), s.charAt(0));
    }
}
