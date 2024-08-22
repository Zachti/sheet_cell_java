package filter.dto;

import range.CellRange;

import java.util.List;

public record MultiColumnsFilterConfig(CellRange range, List<List<Object>> selectedValues, boolean isAnd) {}
