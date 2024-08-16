package xml;

import jaxb.dto.SheetConfiguration;
import menu.enums.SheetOption;
import position.interfaces.IPosition;

public interface IXMLProcessor {
    SheetConfiguration process(SheetOption choice, String filePath) throws Exception;
    void save(String filePath) throws Exception;
    IPosition validatePosition(IPosition position);
}
