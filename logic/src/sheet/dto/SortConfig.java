package sheet.dto;

import range.CellRange;

import java.util.List;

public record SortConfig(CellRange range, List<Character> columns, boolean ascending) {}
