package component.app;

import cell.dto.CellDetails;
import common.enums.SheetOption;
import component.sheet.Enum.PropType;
import connector.Connector;
import dto.SheetDto;
import engine.Engine;
import engine.IEngine;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import component.sheet.SheetController;
import component.top.TopController;
import jaxb.dto.SheetConfiguration;
import position.interfaces.IPosition;
import shticell.sheet.position.IPosition;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.range.Range;
import xml.IXMLProcessor;
import xml.XMLProcessor;

import static validator.FileValidator.validateFileExists;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class AppController {

    public BorderPane root;
    private IEngine engine = new Engine();
    private final IXMLProcessor xmlProcessor = new XMLProcessor();

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
        SheetConfiguration configuration = getSheet(path);
        engine = new Engine(configuration.sheet());
        int rows = configuration.layout().getRows();
        int cols = configuration.layout().getColumns();
        sheetComponentController.fillSheet(configuration.sheet());
        topComponentController.addRangesToComboBox(connector.getRanges().stream().map(Range::rangeName).toList());
    }

//    public void importFile(String path) {
//        try {
//            engine.SetSheet(path);
//            topComponentController.clearVersion();
//            topComponentController.addVersion();
//            sheetComponentController.clearSheet();
//            fillSheet();
//        }
//        catch (Exception e) {
//            topComponentController.setPreviousPath();
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText("An error occurred while importing the file.");
//            alert.setContentText(e.getMessage());
//            Platform.runLater(alert::showAndWait);
//        }
//    }



    private void fillSheet(){
        SheetDto sheet = connector.getSheet();
        numOfCols = sheet.numberOfColumns();
        numOfRows = sheet.numberOfRows();
        sheetComponentController.fillSheet(sheet);
        topComponentController.addRangesToComboBox(connector.getRanges().stream().map(Range::rangeName).toList());
    }

    public void saveSheet(String path) throws IOException {
        connector.InsertSheetToBinaryFile(path);
    }

    public void createNewSheet(String sheetName, int numColumns, int numRows){
        connector.SetSheet(new SheetDto(sheetName,1,numColumns,numRows,100,30));
        sheetComponentController.clearSheet();
        fillSheet();
        topComponentController.clearVersion();
        topComponentController.addVersion();
    }

    public void updateCell(IPosition position,String value) throws LoopConnectionException {
        topComponentController.addVersion();
        connector.UpdateCellByCoordinate(position,value);
    }

    public SheetConfiguration GetSheet(){

        return connector.getSheet();
    }

    public void cellClicked(IPosition position){
        topComponentController.setOnMouseCoordinate(connector.GetCellByCoordinate(position));
        //List<IPosition> influenceOn = connector.getSheet().cells().get(position).influenceOn();
        CellDetails details = engine.getCellDetails(position);
        Set<IPosition> influenceOn = details.observers();
        Set<IPosition> dependsOn = details.observables();
        sheetComponentController.Paint(influenceOn,"Green", PropType.INFLUENCE_ON);
        sheetComponentController.Paint(dependsOn,"Blue",PropType.DEPENDS_ON);
    }

    public void PaintCells(List<IPosition> coordinates,String color){
        sheetComponentController.Paint(coordinates,color,PropType.COLOR);
    }

    public void addRange(String rangeName,IPosition startCoordinate,IPosition endCoordinate){
        connector.AddRange(new Range(rangeName,startCoordinate,endCoordinate));
    }

    public Range GetRange(String rangeName) {
        return connector.GetRangeDto(rangeName);
    }


    public void removePaint(){
        sheetComponentController.removePaint();
    }

    public SheetDto getSheetByVersion(int version){
        return connector.GetSheetByVersion(version);
    }

    public int getNumOfCols(){
        return numOfCols;
    }

    public int getNumOfRows(){
        return numOfRows;
    }

    public List<String> getValuesInColumn(Range range ,int col){
        return connector.getValuesInColumn(range ,col);
    }
    public SheetDto applyFilter(int col, Range range ,List<String> filters) {
        return connector.applyFilter(col,range,filters);
    }
}
