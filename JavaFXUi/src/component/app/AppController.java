package component.app;

import cell.Cell;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import common.enums.SheetOption;
import component.sheet.Enum.PropType;
import engine.Engine;
import engine.IEngine;
import filter.dto.FilterConfig;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import component.sheet.SheetController;
import component.top.TopController;
import jaxb.dto.SheetConfiguration;
import jaxb.generated.STLLayout;
import jaxb.generated.STLSize;
import position.interfaces.IPosition;
import range.CellRange;
import sheet.Sheet;
import sheet.dto.CreateSheetDto;
import sheet.interfaces.ISheet;
import xml.IXMLProcessor;
import xml.XMLProcessor;

import static validator.FileValidator.validateFileExists;

import java.util.*;

public class AppController {

    public BorderPane root;
    private IEngine engine;
    private final IXMLProcessor xmlProcessor = new XMLProcessor();
    private SheetConfiguration sheetConfiguration;

    @FXML private TopController topComponentController;
    @FXML private SheetController sheetComponentController;


    private int numOfCols;
    private int numOfRows;

    @FXML
    public void initialize() {
        topComponentController.setAppController(this);
        sheetComponentController.setAppController(this);
    }

    public SheetConfiguration getSheet(String path) {
        try {
            validateFileExists(path);
            return xmlProcessor.parse(SheetOption.NEW, path);
        } catch (Exception e) {
            System.out.println("The file content is invalid. Please provide a valid existing sheet content file.");
            System.out.println(e.getMessage());
            System.out.println("Please try again.");
            return getSheet(path);
        }
    }

    private void loadSheet(String path) {
        sheetConfiguration = getSheet(path);
        engine = new Engine(sheetConfiguration.sheet());
        numOfRows = sheetConfiguration.layout().getRows();
        numOfCols = sheetConfiguration.layout().getColumns();
        sheetComponentController.fillSheet(sheetConfiguration);
        topComponentController.addRangesToComboBox(engine.getRanges().stream().map(CellRange::name).toList());
        // todo - leibo where do you use this method ?
    }

    public void createNewSheet(String sheetName, int numColumns, int numRows){
        CreateSheetDto createSheetDto = new CreateSheetDto(sheetName, numColumns, numRows, null);
        ISheet sheet = new Sheet(createSheetDto);
        STLLayout layout = new STLLayout();
        layout.setRows(numRows);
        layout.setColumns(numColumns);
        STLSize uiSize = new STLSize();
        uiSize.setRowsHeightUnits(30);
        uiSize.setColumnWidthUnits(100);
        SheetConfiguration configuration = new SheetConfiguration(sheet, uiSize, layout);
        engine = new Engine(sheet);
        sheetComponentController.clearSheet();
        sheetComponentController.fillSheet(configuration);
        topComponentController.clearVersion();
        topComponentController.addVersion();
    }

    public void updateCell(IPosition position,String newValue) {
        topComponentController.addVersion();
        engine.updateCell(new UpdateCellDto(position, newValue));
    }

    public SheetConfiguration getSheetConfiguration(){ return sheetConfiguration; }

    public void cellClicked(IPosition position){
        topComponentController.setOnMouseCoordinate(engine.getCellDetails(position));
        CellDetails details = engine.getCellDetails(position);
        Set<IPosition> observers = details.observers();
        Set<IPosition> observables = details.observables();
        sheetComponentController.Paint(observers,"Green", PropType.INFLUENCE_ON);
        sheetComponentController.Paint(observables,"Blue",PropType.DEPENDS_ON);
    }

    public void PaintCells(Set<IPosition> positions, String color){
        sheetComponentController.Paint(positions,color,PropType.COLOR);
    }

    public void addRange(String rangeName,IPosition startCoordinate,IPosition endCoordinate){
        engine.addRange(new CellRange(rangeName,startCoordinate,endCoordinate));
    }

    public void removePaint(){
        sheetComponentController.removePaint();
    }

    public Map<IPosition, Cell> getSheetByVersion(int version){
        return engine.getHistory(version);
    }

    public int getNumOfCols(){
        return numOfCols;
    }

    public int getNumOfRows(){
        return numOfRows;
    }

    public List<String> getValuesInColumn(CellRange range){
        return engine.viewCellsInRange(range).stream().map(Cell::getEffectiveValue).toList();
    }

    public Map<IPosition,Cell> getRowsByFilter(CellRange range , List<Object> selectedValues) {
        return engine.getCellsByFilter(new FilterConfig(range,selectedValues));
        // todo - change it to give the map of <Iposition,Cell> in those rows
    }

    public CellRange getRange(String selectedItem) {
        return engine.getRanges().stream().filter(range -> Objects.equals(range.name(), selectedItem)).toList().getFirst();
    }

    public void saveSheet(String path) throws Exception {
        xmlProcessor.save(path);
    }
}
