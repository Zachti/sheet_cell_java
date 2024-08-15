package xml;

import jaxb.generated.UiSheet;
import menu.enums.SheetOption;
import position.interfaces.IPosition;

public interface IXMLProcessor {
    UiSheet process(SheetOption choice, String filePath) throws Exception;
    void save(String filePath) throws Exception;
    IPosition validatePosition(IPosition position);
}
