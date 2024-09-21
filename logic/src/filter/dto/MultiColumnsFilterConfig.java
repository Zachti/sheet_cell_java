package filter.dto;

import range.IRange;

import java.util.List;
import java.util.Map;

public record MultiColumnsFilterConfig(IRange range, Map<Character, List<String>> selectedValues, boolean isAnd) {}
