package jaxb.uiSheetFactory;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CopyCellDto;
import cell.dto.CreateCellDto;
import jaxb.generated.STLCell;
import jaxb.generated.STLPosition;
import jaxb.generated.STLSheet;
import jaxb.generated.UiSheet;
import position.PositionFactory;
import position.interfaces.IPosition;
import sheet.dto.CopySheetDto;
import sheet.dto.CreateSheetDto;

import java.util.*;
import java.util.stream.Collectors;

public final class ExistingSheetFactory extends NewSheetFactory {

    @Override
    protected UiSheet createSheet() {
        CreateSheetDto createSheetDto = super.xmlSheetToCreateSheetDto(sheet);
        Map<Integer, Integer> version2Count = listVersionCountToMapVersionCount(sheet.getSTLVersion2Count().getEntry());
        CopySheetDto copySheetDto = new CopySheetDto(createSheetDto, sheet.getVersion(), version2Count);
        return new UiSheet(copySheetDto, sheet.getSTLLayout().getSTLSize(), sheet.getSTLLayout());
    }

    private Map<Integer, Integer> listVersionCountToMapVersionCount(List<STLSheet.STLVersion2Count.Entry> list) {
        return list.stream()
                .collect(Collectors.toMap(
                        STLSheet.STLVersion2Count.Entry::getKey,
                        STLSheet.STLVersion2Count.Entry::getValue,
                        (existing, _) -> existing,
                        TreeMap::new));
    }

    @Override
    protected void createCell(STLCell stlCell, List<Cell> cells) {
        cells.add(new Cell(stlCellToCopyCellDto(stlCell)));
    }

    private Set<IPosition> listPositionToSetPosition(List<STLPosition> list) {
        return list.stream()
                .map(position -> new position.Position(position.getRow(), position.getColumn().charAt(0)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private TreeMap<Integer, CellBasicDetails> listHistoryToTreeMapHistory(List<STLCell.STLCellHistory.Entry> list) {
        return list.stream()
                .map(this::argsToEntry)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, _) -> existing,
                        TreeMap::new));
    }

    private Map.Entry<Integer, CellBasicDetails> argsToEntry(STLCell.STLCellHistory.Entry entry) {
        Cell cell = Cell.fromBasicDetails(createCellBasicDetails(entry.getSTLCell()));
        return new AbstractMap.SimpleEntry<>(entry.getKey(), cell.getBasicDetails());
    }

    private CopyCellDto stlCellToCopyCellDto(STLCell stlCell) {
        CreateCellDto createCellDto = stlCellToCreateCellDto(stlCell);
        Set<IPosition> observers = listPositionToSetPosition(stlCell.getObservers().getPosition());
        Set<IPosition> observables = listPositionToSetPosition(stlCell.getObservables().getPosition());
        return new CopyCellDto(createCellDto,observers,observables, listHistoryToTreeMapHistory(stlCell.getSTLCellHistory().getEntry()));
    }

    private CellBasicDetails createCellBasicDetails(STLCell stlCell) {
        IPosition pos = PositionFactory.create(stlCell.getRow(), stlCell.getColumn().charAt(0));
        return new CellBasicDetails(pos, stlCell.getSTLOriginalValue(), stlCell.getEffectiveValue());
    }
}
