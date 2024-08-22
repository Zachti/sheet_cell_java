package filter.dto;

import range.CellRange;

import java.util.List;

public record FilterConfig(CellRange range, List<Object> selectedValues) {}
