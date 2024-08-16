package sheet.dto;

import cell.Cell;

import java.util.List;

public record CreateSheetDto(String name, int numberOfRows, int numberOfCols, List<Cell> cells) {}
