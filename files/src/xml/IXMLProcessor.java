package xml;

import common.enums.SheetOption;
import jaxb.dto.SheetConfiguration;
import position.interfaces.IPosition;

public interface IXMLProcessor {
    SheetConfiguration parse(SheetOption choice, String filePath) throws Exception;
    void save(String filePath) throws Exception;
    IPosition validatePosition(IPosition position);
    void setSheetConfiguration(SheetConfiguration sheetConfiguration);
}
