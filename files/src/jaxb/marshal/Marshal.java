package jaxb.marshal;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellXmlPayload;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jaxb.generated.*;
import position.interfaces.IPosition;
import store.TypedContextStore;

import java.io.File;
import java.util.*;

public final class Marshal implements IWriter {

    @Override
    public void saveSheet(UiSheet sheet, String filePath) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(STLSheet.class);
        File outputXmlFile = new File(filePath);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(uiSheetToStlSheet(sheet), outputXmlFile);
    }

    private STLSheet uiSheetToStlSheet(UiSheet sheet) {
        TypedContextStore.getSheetStore().setContext(sheet);
        STLSheet stlSheet = new STLSheet();
        stlSheet.setName(sheet.getName());
        stlSheet.setSTLLayout(sheet.getLayout());
        stlSheet.setSTLCells(cellsToStlCells(sheet.getCells().values().stream().toList()));
        stlSheet.setVersion(sheet.getVersion());
        stlSheet.setSTLVersion2Count(createVersionToCount(sheet.getUpdate2VersionCount()));
        TypedContextStore.getSheetStore().clearContext();
        return stlSheet;
    }

    private STLCells cellsToStlCells(List<Cell> cells) {
        STLCells stlCells = new STLCells();
        cells.forEach(cell -> insertStlCell(cell, stlCells));
        return stlCells;
    }

    private void insertStlCell(Cell cell, STLCells stlCells) {
        STLCell stlCell = cellToStlCell(cell);
        CellXmlPayload payload = cell.getCellXmlPayload();
        STLCell.STLCellHistory history = generateHistoryList(payload.history());
        stlCell.setSTLCellHistory(history);
        stlCell.setObservers(positionSetToList(payload.observers()));
        stlCell.setObservables(positionSetToList(payload.observables()));
        stlCells.getSTLCell().add(stlCell);
    }

    private STLCell.STLCellHistory generateHistoryList(TreeMap<Integer, CellBasicDetails> history) {
        STLCell.STLCellHistory historyList = new STLCell.STLCellHistory();
        history.forEach((version, cell) -> historyList.getEntry().add(argsToXmlHistoryEntry(Map.entry(version, cell))));
        return historyList;
    }

    private STLCell cellToStlCell(Cell cell) {
        STLCell stlCell = new STLCell();
        stlCell.setSTLOriginalValue(cell.getOriginalValue());
        stlCell.setRow(cell.getPosition().row());
        stlCell.setColumn(String.valueOf(cell.getPosition().column()));
        stlCell.setEffectiveValue(cell.getEffectiveValue());
        return stlCell;
    }

    private List<STLSheet.STLVersion2Count.Entry> versionCountMapTolist(Map<Integer, Integer> version2updateCount) {
        List<STLSheet.STLVersion2Count.Entry> version2updateList = new LinkedList<>();
        version2updateCount.forEach((version, updateCount) ->
                version2updateList.add(versionCountToEntry(Map.entry(version, updateCount))));
        return version2updateList;
    }

    private PositionList positionSetToList(Set<IPosition> positionSet) {
        PositionList positionList = new PositionList();
        positionSet.forEach(position -> positionList.getPosition().add(IPositionToXmlPosition(position)));
        return positionList;
    }

    private STLSheet.STLVersion2Count createVersionToCount(Map<Integer, Integer> version2updateCount) {
        STLSheet.STLVersion2Count version2Count = new STLSheet.STLVersion2Count();
        version2Count.getEntry().addAll(versionCountMapTolist(version2updateCount));
        return version2Count;
    }

    private STLPosition IPositionToXmlPosition(IPosition position) {
        STLPosition pos = new STLPosition();
        pos.setRow(position.row());
        pos.setColumn(String.valueOf(position.column()));
        return pos;
    }

    private STLSheet.STLVersion2Count.Entry versionCountToEntry(Map.Entry<Integer, Integer> entry) {
        STLSheet.STLVersion2Count.Entry version2CountEntry = new STLSheet.STLVersion2Count.Entry();
        version2CountEntry.setKey(entry.getKey());
        version2CountEntry.setValue(entry.getValue());
        return version2CountEntry;
    }

    private STLCell.STLCellHistory.Entry argsToXmlHistoryEntry(Map.Entry<Integer, CellBasicDetails> entry) {
        STLCell.STLCellHistory.Entry historyEntry = new STLCell.STLCellHistory.Entry();
        historyEntry.setKey(entry.getKey());
        historyEntry.setSTLCell(cellToStlCell(Cell.fromBasicDetails(entry.getValue())));
        return historyEntry;
    }
}
