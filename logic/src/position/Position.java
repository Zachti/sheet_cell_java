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

        return PositionFactory.create(s);
    }

    public static String getColumnLabel(int colIndex) {
        StringBuilder columnLabel = new StringBuilder();
        while (colIndex > 0) {
            colIndex--;
            columnLabel.insert(0, (char) ('A' + (colIndex % 26)));
            colIndex /= 26;
        }
        return columnLabel.toString();
    }
}
