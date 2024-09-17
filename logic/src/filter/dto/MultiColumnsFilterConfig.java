package filter.dto;

import range.CellRange;

import java.util.List;
import java.util.Map;

public record MultiColumnsFilterConfig(CellRange range, List<Map<Character, String>> selectedValues, boolean isAnd) {}
