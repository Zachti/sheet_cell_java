package jaxb.dto;

import jaxb.generated.STLLayout;
import jaxb.generated.STLSize;
import sheet.interfaces.ISheet;

public record SheetConfiguration(ISheet sheet, STLSize uiUnits, STLLayout layout) {}
