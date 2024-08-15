package sheet.builder;

import cell.Cell;
import cell.dto.CreateCellDto;
import position.PositionFactory;
import position.interfaces.IPosition;
import sheet.builder.enums.MaxDimensions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static common.utils.InputValidation.isInRange;
import static common.utils.InputValidation.validateOrThrow;

public class SheetBuilder implements IBuilder {
    private final int numberOfRows;
    private final int numberOfCols;

    public SheetBuilder(int rows, int cols) {
        validateDimensions(rows, cols);
        this.numberOfRows = rows;
        this.numberOfCols = cols;
    }

    @Override
    public LinkedHashMap<IPosition, Cell> build() {
        return IntStream.rangeClosed(1, numberOfCols)
                .boxed()
                .flatMap(col -> IntStream.rangeClosed(1, numberOfRows)
                        .mapToObj(row -> createMapEntry(row, col)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, _) -> existing,
                        LinkedHashMap::new));
    }

    @Override
    public int getNumberOfRows() {
        return numberOfRows;
    }

    @Override
    public int getNumberOfCols() {
        return numberOfCols;
    }

    @Override
    public SheetBuilder clone() {
        try {
            return (SheetBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("SheetBuilder clone failed");
        }
    }

    private Map.Entry<IPosition, Cell> createMapEntry(int row, int col) {
        char column = intColToCharCol(col);
        IPosition position = PositionFactory.create(row, column);
        Cell cell = new Cell(new CreateCellDto("", row, column));
        return Map.entry(position, cell);
    }

    public static char intColToCharCol(int column) {
        return (char) ('A' + column - 1);
    }

    private static void validateDimensions(int rows, int cols) {
        validateOrThrow(
                new int[]{rows, cols},
                dimensions -> isInRange(dimensions[0], 1, MaxDimensions.MAX_ROWS.getValue()) && isInRange(dimensions[1], 1,  MaxDimensions.MAX_COLS.getValue()),
                _ -> "Invalid dimensions! Rows must be between 1 and " +  MaxDimensions.MAX_ROWS.getValue() + ", and cols between 1 and " + MaxDimensions.MAX_COLS.getValue()
        );
    }
}
