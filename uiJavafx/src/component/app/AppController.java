package component.app;

import cell.Cell;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import common.enums.SheetOption;
import component.sheet.Enum.PropType;
import engine.Engine;
import engine.IEngine;
import filter.dto.FilterConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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


    public void getSheet(String path) {
        try {
            validateFileExists(path);
            topComponentController.clearVersion();
            topComponentController.addVersion();
            sheetComponentController.clearSheet();
            SheetConfiguration sc = xmlProcessor.parse(SheetOption.NEW, path);
            loadSheet(sc);

        } catch (Exception e) {
            topComponentController.setPreviousPath();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred while importing the file.");
            alert.setContentText(e.getMessage());
            Platform.runLater(alert::showAndWait);

        }
    }

    private void loadSheet(SheetConfiguration sc) {
        sheetConfiguration = sc;
        engine = new Engine(sheetConfiguration.sheet());
        numOfRows = sheetConfiguration.layout().getRows();
        numOfCols = sheetConfiguration.layout().getColumns();
        sheetConfiguration.uiUnits().setRowsHeightUnits(30);
        sheetConfiguration.uiUnits().setColumnWidthUnits(100);
        sheetComponentController.fillSheet(sheetConfiguration);
        topComponentController.addRangesToComboBox(engine.getRanges().stream().map(CellRange::name).toList());
        topComponentController.EnableButtons();
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
        layout.setSTLSize(uiSize);
        sheetConfiguration = new SheetConfiguration(sheet, uiSize, layout);
        numOfRows = sheetConfiguration.layout().getRows();
        numOfCols = sheetConfiguration.layout().getColumns();
        xmlProcessor.setSheetConfiguration(sheetConfiguration);
        engine = new Engine(sheet);
        sheetComponentController.clearSheet();
        sheetComponentController.fillSheet(sheetConfiguration);
        topComponentController.clearVersion();
        topComponentController.addVersion();
    }

    public void updateCell(IPosition position,String newValue) {
        topComponentController.addVersion();
        engine.updateCell(new UpdateCellDto(position, newValue));
    }

    public SheetController getSheetComponentController() {
        return sheetComponentController;
    }

    public TopController getTopComponentController() {
        return topComponentController;
    }

    public SheetConfiguration getSheetConfiguration(){ return sheetConfiguration; }

    public void cellClicked(IPosition position){
        topComponentController.setOnMouseCoordinate(engine.getCellDetails(position));
        CellDetails details = engine.getCellDetails(position);
        Set<IPosition> observers = details.observers();
        Set<IPosition> observables = details.observables();
        sheetComponentController.removePaint();
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
        if (sheetConfiguration == null) {
            throw new IllegalStateException("SheetConfiguration is not initialized.");
        }
        xmlProcessor.save(path);
    }
}
