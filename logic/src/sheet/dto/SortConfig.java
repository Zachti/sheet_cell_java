package sheet.dto;

import range.IRange;

import java.util.List;

public record SortConfig(IRange range, List<Character> columns, boolean ascending) {}
