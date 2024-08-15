package jaxb.generated;

public final class UiUnits extends STLSize {

    public UiUnits(STLSize size) {
        columnWidthUnits = size.getColumnWidthUnits();
        rowsHeightUnits = size.getRowsHeightUnits();
    }

    public void addToColumnWidthUnits(int columnWidthUnits) { this.columnWidthUnits += columnWidthUnits; }
}
