package jaxb.marshal;

import jaxb.dto.SheetConfiguration;

public interface IWriter {
    void writeSheet(SheetConfiguration configuration, String filePath) throws Exception;
}
