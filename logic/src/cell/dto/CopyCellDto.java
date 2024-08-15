package cell.dto;

import position.interfaces.IPosition;

import java.util.Set;
import java.util.TreeMap;

public record CopyCellDto(
        CreateCellDto createCellDto,
        Set<IPosition> observers,
        Set<IPosition> observables,
        TreeMap<Integer, CellBasicDetails> versionHistory) {}
