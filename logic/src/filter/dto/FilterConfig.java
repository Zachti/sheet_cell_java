package filter.dto;

import range.IRange;

import java.util.Map;

public record FilterConfig(IRange range, Map<Character, String> selectedValues) {}
