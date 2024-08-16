package position;

import java.util.HashMap;
import java.util.Map;

public class PositionFactory {

    private static final Map<String, Position> cachedPositions = new HashMap<>();

    public static Position create(int row, char column) {
        String key = row + ":" + column;

        return cachedPositions.computeIfAbsent(key, _ -> new Position(row, column));
    }

    private PositionFactory() {}
}
