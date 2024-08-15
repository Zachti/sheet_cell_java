package cell.tree.enums;

import java.util.Arrays;
import java.util.Optional;

public enum InputSymbols {
    OPEN_BRACE('{'),
    CLOSE_BRACE('}'),
    COMMA(',');

    private final char character;

    InputSymbols(char character) {
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }

    public static Optional<InputSymbols> fromChar(char c) {
        return Arrays.stream(InputSymbols.values())
                .filter(symbol -> symbol.getCharacter() == c)
                .findFirst();
    }
}
