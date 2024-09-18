package sheet.dto;

import cell.Cell;
import range.IRange;

import java.util.List;
import java.util.Map;

public record CreateSheetDto(String name, int numberOfRows, int numberOfCols, List<Cell> cells, Map<String, IRange> ranges) {}
