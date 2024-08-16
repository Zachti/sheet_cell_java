package xml;

import common.enums.SheetOption;
import jaxb.dto.SheetConfiguration;
import jaxb.marshal.Marshal;
import jaxb.sheetConfigurationFactory.SheetConfigurationFactory;
import position.interfaces.IPosition;
import xml.dto.PositionDetails;

import static common.utils.InputValidation.isInRange;
import static common.utils.InputValidation.validateOrThrow;

public final class XMLProcessor implements IXMLProcessor {
    Marshal marshal = new Marshal();
    SheetConfiguration sheetConfiguration;

    @Override
    public SheetConfiguration parse(SheetOption choice, String filePath) throws Exception {
        sheetConfiguration = SheetConfigurationFactory.newInstance(choice).create(filePath);
        return sheetConfiguration;
    }

    @Override
    public void save(String filePath) throws Exception {
        marshal.writeSheet(sheetConfiguration, filePath);
    }

    @Override
    public IPosition validatePosition(IPosition position) {
        char lastCol = (char)('A' + sheetConfiguration.layout().getColumns() - 1);
        int lastRow = sheetConfiguration.layout().getRows();
        validateOrThrow(
                new PositionDetails(position.column(), position.row(), lastCol, lastRow),
                details -> isInRange(details.column(), 'A', details.lastCol()) && isInRange(details.row(), 1, details.lastRow()),
                details -> "Invalid position. Columns must be between A and " + details.lastCol() + ", and rows must be between 1 and " + details.lastRow()
        );
        return position;
    }
}
