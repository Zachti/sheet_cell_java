package cell.dto;

import position.interfaces.IPosition;

public record CellBasicDetails(IPosition position, Object originalValue, Object effectiveValue) {

    @Override
    public String toString() {
        return String.format(
                "cell position: %s%n" +
                "original value: %s%n" +
                "effective value: %s%n",
                position,
                originalValue,
                effectiveValue
        );
    }
}
