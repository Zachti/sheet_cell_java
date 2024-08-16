package xml;

import common.enums.SheetOption;
import jaxb.dto.SheetConfiguration;
import position.interfaces.IPosition;

public interface IXMLProcessor {
    SheetConfiguration process(SheetOption choice, String filePath) throws Exception;
    void save(String filePath) throws Exception;
    IPosition validatePosition(IPosition position);
}
