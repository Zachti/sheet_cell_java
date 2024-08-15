package jaxb.marshal;

import jaxb.generated.UiSheet;

public interface IWriter {
    void saveSheet(UiSheet sheet, String filePath) throws Exception;
}
