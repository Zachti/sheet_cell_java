package xml;

import dto.PositionDetails;
import jaxb.generated.UiSheet;
import jaxb.marshal.Marshal;
import jaxb.uiSheetFactory.SheetFactory;
import menu.enums.SheetOption;
import position.interfaces.IPosition;

import static common.utils.InputValidation.isInRange;
import static common.utils.InputValidation.validateOrThrow;

public final class XMLProcessor implements IXMLProcessor {
    Marshal marshal = new Marshal();
    UiSheet uiSheet;

    @Override
    public UiSheet process(SheetOption choice, String filePath) throws Exception {
        SheetFactory uiSheetFactory = SheetFactory.newInstance(choice);
        uiSheet = uiSheetFactory.create(filePath);
        return uiSheet;
    }

    @Override
    public void save(String filePath) throws Exception {
        marshal.saveSheet(uiSheet, filePath);
    }

    @Override
    public IPosition validatePosition(IPosition position) {
        char lastCol = (char)('A' + uiSheet.getLayout().getColumns() - 1);
        int lastRow = uiSheet.getLayout().getRows();
        validateOrThrow(
                new PositionDetails(position.column(), position.row(), lastCol, lastRow),
                details -> isInRange(details.column(), 'A', details.lastCol()) && isInRange(details.row(), 1, details.lastRow()),
                details -> "Invalid position. Columns must be between A and " + details.lastCol() + ", and rows must be between 1 and " + details.lastRow()
        );
        return position;
    }
}
