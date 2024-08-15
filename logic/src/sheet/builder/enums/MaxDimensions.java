package sheet.builder.enums;

public enum MaxDimensions {
    MAX_ROWS(50),
    MAX_COLS(20);

    private final int value;

    MaxDimensions(int value) { this.value = value; }

    public int getValue() { return value; }
}
