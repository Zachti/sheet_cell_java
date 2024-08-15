package sheet.dto;

import cell.Cell;

import java.util.LinkedList;

public record CreateSheetDto(String name, int numberOfRows, int numberOfCols, LinkedList<Cell> cells) {}
