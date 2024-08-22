package drawer;

import cell.Cell;
import jaxb.generated.STLSize;
import position.PositionFactory;
import position.interfaces.IPosition;

import java.util.Map;
import java.util.stream.IntStream;

import static sheet.builder.SheetBuilder.intColToCharCol;

public final class SheetDrawer extends Drawer {
    private final STLSize uiUnits;
    private final int numberOfRows;
    private final int numberOfColumns;
    private final Map<IPosition, Cell> position2Cell;
    private final static String END_OF_LINE = " |";
    private final static String SHORT_END_OF_LINE = "|";
    private final static String EMPTY_LINE = "";

    public SheetDrawer(STLSize uiUnits, int numberOfRows, int numberOfColumns, Map<IPosition, Cell> position2Cell) {
        this.position2Cell = position2Cell;
        this.uiUnits = uiUnits;
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
    }

    public SheetDrawer(SheetDrawer sheetDrawer, Map<IPosition, Cell> position2Cell) {
        this.uiUnits = sheetDrawer.uiUnits;
        this.numberOfRows = sheetDrawer.numberOfRows;
        this.numberOfColumns = sheetDrawer.numberOfColumns;
        this.position2Cell = position2Cell;
    }

    @Override
    public void draw() {
        drawTopLettersRow();
        IntStream.range(1, numberOfRows + 1).forEach(this::drawRow);
        display(EMPTY_LINE);
    }

    public void drawHistory(Map<IPosition, Cell> historicPosition2Cell) {
        new SheetDrawer(this, historicPosition2Cell).draw();
    }

    private void drawTopLettersRow() {
        StringBuilder topLetterRow = new StringBuilder();
        int padding = uiUnits.getColumnWidthUnits();
        topLetterRow.append(" ").append(END_OF_LINE);

        IntStream.range(0, numberOfColumns)
                .forEach(i -> topLetterRow.append(centralizedValue(String.valueOf((char) (i + 'A')), padding + 1))
                        .append(END_OF_LINE));

        display(topLetterRow.toString());
    }

    private void drawRow(int row) {
        StringBuilder content = new StringBuilder(getRowIndexStrategy(row));
        int columnWidth = uiUnits.getColumnWidthUnits();

        IntStream.range(1, numberOfColumns + 1)
                .forEach(col -> drawCell(row, content, columnWidth, col));

        display(content.toString());
    }

    private void drawCell(int row, StringBuilder content, int columnWidth, int col) {
        String value = getEffectiveValue(row, col);
        content.append(" ")
                .append(centralizedValue(value, columnWidth))
                .append(END_OF_LINE);
    }

    private String getEffectiveValue(int row , int col) {
        IPosition position = PositionFactory.create(row, intColToCharCol(col));
        Cell cell = position2Cell.get(position);
        return cell.getEffectiveValue();
    }

//    private int calculateMaxCellValueLength() {
//        return position2Cell.values().stream()
//                .map(Cell::getEffectiveValue)
//                .map(String::length)
//                .max(Integer::compareTo)
//                .orElse(0);
    // todo - ask if can be implemented

//    }


    private String getRowIndexStrategy(int rowIndex) {
        return rowIndex + (rowIndex > 9 ? SHORT_END_OF_LINE : END_OF_LINE);
    }
}
