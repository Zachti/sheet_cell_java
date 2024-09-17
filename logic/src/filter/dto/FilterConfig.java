package filter.dto;

import range.CellRange;

import java.util.Map;

public record FilterConfig(CellRange range, Map<Character, String> selectedValues) {}
