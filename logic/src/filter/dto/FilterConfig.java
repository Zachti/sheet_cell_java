package filter.dto;

import range.IRange;

import java.util.List;
import java.util.Map;

public record FilterConfig(IRange range, Map<Character, List<String>> selectedValues) {}
