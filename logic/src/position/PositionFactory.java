package position;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PositionFactory {

    private static final Map<String, Position> cachedPositions = new HashMap<>();

    public static Position create(int row, char column) {
        String key = row + ":" + column;

        return cachedPositions.computeIfAbsent(key, value -> new Position(row, column));
    }

    public static Position create(String pos) {
        char col = pos.charAt(0);
        int row = Character.getNumericValue(pos.charAt(1));
        return create(row, col);
    }

    public static boolean isValidCoordinate(String coordinate) {
        String pattern = "^[a-zA-Z]+\\d+$";
        return coordinate.matches(pattern);
    }

    public static boolean isValidCoordinate(String[] coordinates) {
        return Arrays.stream(coordinates)
                .allMatch(PositionFactory::isValidCoordinate);
    }

    private PositionFactory() {}
}
