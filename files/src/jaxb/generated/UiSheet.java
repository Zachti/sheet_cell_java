package jaxb.generated;

import cell.Cell;
import position.interfaces.IPosition;
import sheet.Sheet;
import sheet.dto.CopySheetDto;
import sheet.dto.CreateSheetDto;

import java.util.Map;

public final class UiSheet extends Sheet {
    private final STLSize uiUnits;
    private final STLLayout layout;

    public UiSheet(CreateSheetDto createSheetDto, STLSize uiUnits, STLLayout layout) {
        super(createSheetDto);
        this.uiUnits = uiUnits;
        this.layout = layout;
    }

    public UiSheet(CopySheetDto copySheetDto, STLSize uiUnits, STLLayout layout) {
        super(copySheetDto);
        this.uiUnits = uiUnits;
        this.layout = layout;
    }

    public STLSize getUiUnits() { return uiUnits; }

    public Map<IPosition, Cell> getCells() { return cellManager.getCells(); }

    public STLLayout getLayout() { return layout; }

    public int getVersion() { return version; }
}
