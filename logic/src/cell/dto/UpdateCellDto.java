package cell.dto;

import position.interfaces.IPosition;

public record UpdateCellDto(IPosition position, Object newOriginalValue) {}
