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

    private LinkedList<Cell> xmlCellsToCells(List<STLCell> stlCells) {
        LinkedList<Cell> cells = new LinkedList<>();
        stlCells.forEach(stlCell -> safeCreateCell(stlCell, cells));
        return cells;
    }

    protected CreateSheetDto xmlSheetToCreateSheetDto(STLSheet sheet) {
        String name = sheet.getName();
        int rows = sheet.getSTLLayout().getRows();
        int cols = sheet.getSTLLayout().getColumns();
        LinkedList<Cell> cells = xmlCellsToCells(sheet.getSTLCells().getSTLCell());
        return new CreateSheetDto(name, rows, cols, cells);
    }

    @Override
    protected void createCell(STLCell stlCell, LinkedList<Cell> cells) {
        cells.add(new Cell(stlCellToCreateCellDto(stlCell)));
    }

    protected CreateCellDto stlCellToCreateCellDto(STLCell stlCell) {
        String value = stlCell.getSTLOriginalValue();
        return new CreateCellDto(value, stlCell.getRow(), stlCell.getColumn().charAt(0));
    }
}
