package jaxb.marshal;

import jaxb.dto.SheetConfiguration;

public interface IWriter {
    void saveSheet(SheetConfiguration configuration, String filePath) throws Exception;
}
