package sheet.dto;

import java.util.Map;

public record CopySheetDto(CreateSheetDto createSheetDto, int Version, Map<Integer, Integer> version2updateCount) {}
