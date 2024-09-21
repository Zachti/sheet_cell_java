package filter.dto;

import range.IRange;

import java.util.List;

public record FilterConfig(IRange range, List<String> selectedValues) {}
