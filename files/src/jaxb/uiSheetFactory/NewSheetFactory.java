package jaxb.uiSheetFactory;

import cell.Cell;
import cell.dto.CreateCellDto;
import jaxb.generated.STLCell;
import jaxb.generated.STLSheet;
import jaxb.generated.UiSheet;
import sheet.dto.CreateSheetDto;

import java.util.LinkedList;
import java.util.List;

public class NewSheetFactory extends SheetFactory {

    @Override
    protected UiSheet createSheet () {
        return new UiSheet(xmlSheetToCreateSheetDto(sheet), sheet.getSTLLayout().getSTLSize() ,sheet.getSTLLayout());
    }

    private List<Cell> xmlCellsToCells(List<STLCell> stlCells) {
        List<Cell> cells = new LinkedList<>();
        stlCells.forEach(stlCell -> safeCreateCell(stlCell, cells));
        return cells;
    }

    protected CreateSheetDto xmlSheetToCreateSheetDto(STLSheet sheet) {
        String name = sheet.getName();
        int rows = sheet.getSTLLayout().getRows();
        int cols = sheet.getSTLLayout().getColumns();
        List<Cell> cells = xmlCellsToCells(sheet.getSTLCells().getSTLCell());
        safeExecute(() -> cells.forEach(Cell::setEffectiveValue), cells);
        return new CreateSheetDto(name, rows, cols, cells);
    }

    @Override
    protected void createCell(STLCell stlCell, List<Cell> cells) {
        cells.add(new Cell(stlCellToCreateCellDto(stlCell)));
    }

    protected CreateCellDto stlCellToCreateCellDto(STLCell stlCell) {
        String value = stlCell.getSTLOriginalValue();
        return new CreateCellDto(value, stlCell.getRow(), stlCell.getColumn().charAt(0));
    }
}
