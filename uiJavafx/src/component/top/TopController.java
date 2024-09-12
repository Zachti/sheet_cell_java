package component.top;

import component.app.AppController;
import component.sheet.SheetController;
import component.top.dialog.filter.FilterDialogController;
import component.top.dialog.sheet.SheetDialogController;
import component.top.dialog.range.RangeDialogController;
import dto.CellDto;
import dto.SheetDto;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shticell.sheet.coordinate.Coordinate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TopController {

    private AppController appController;

    String previousPath;

    @FXML
    public MenuItem saveButton;
    @FXML
    private TextField pathTextField;
    @FXML
    private TextField originalValueTextField;
    @FXML
    private TextField lastUpdateTextField;
    @FXML
    private TextField cellIdTextField;
    @FXML
    private ComboBox SheetVersionComboBox;
    @FXML
    private ComboBox rangesComboBox;
    @FXML
    private Button plus;
    @FXML
    private Button minus;
    @FXML
    private Button addFilter;


    private final SimpleBooleanProperty isSheetLoaded = new SimpleBooleanProperty(false);
    private final SimpleStringProperty originalValue = new SimpleStringProperty("");
    private final SimpleIntegerProperty lastUpdate = new SimpleIntegerProperty(0);
    private final SimpleStringProperty cellId = new SimpleStringProperty("");
    private final SimpleStringProperty path = new SimpleStringProperty("");

    @FXML
    public void initialize() {
        pathTextField.textProperty().bind(path);
        originalValueTextField.textProperty().bind(originalValue);
        lastUpdateTextField.textProperty().bind(Bindings.format("%d", lastUpdate));
        cellIdTextField.textProperty().bind(cellId);
        saveButton.disableProperty().bind(isSheetLoaded.not());
        SheetVersionComboBox.disableProperty().bind(isSheetLoaded.not());
        rangesComboBox.disableProperty().bind(isSheetLoaded.not());
        rangesComboBox.setOnAction(event -> {
            if (((String) rangesComboBox.getValue()).isEmpty()) {
                appController.removePaint();
            } else {
                appController.removePaint();
                handleRangeSelected((String) rangesComboBox.getValue());
            }
        });

        rangesComboBox.setVisibleRowCount(5);
        SheetVersionComboBox.setVisibleRowCount(5);
        plus.disableProperty().bind(isSheetLoaded.not());
        minus.disableProperty().bind(isSheetLoaded.not());
        addFilter.disableProperty().bind(isSheetLoaded.not());
        pathTextField.styleProperty().unbind();
        SheetVersionComboBox.getItems().add("Version");
        SheetVersionComboBox.setOnAction(event -> {
            if (!SheetVersionComboBox.getValue().equals("Version")) {
                handleVersionSelected((String) SheetVersionComboBox.getValue());
                SheetVersionComboBox.setValue("Version");
            }
        });
    }


    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    private void handleLoadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML or BIN Files", "*.xml", "*.bin"));
        File initialDirectory = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(initialDirectory);
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            previousPath = path.get();
            path.set(filePath);
            appController.getSheet(filePath);
        }
    }

    @FXML
    void HandleSaveFile() throws IOException {
        if (path.get().endsWith(".xml") || path.get().isEmpty()) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select XML file");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selectedDirectory = directoryChooser.showDialog(null);

            if (selectedDirectory != null) {
                TextInputDialog filenameDialog = new TextInputDialog();
                filenameDialog.setTitle("Enter Filename");
                filenameDialog.setHeaderText("Enter the name of the file:");
                filenameDialog.setContentText("Filename:");
                Optional<String> result = filenameDialog.showAndWait();
                if (result.isPresent()) {
                    String filename = result.get();
                    if (!filename.isEmpty()) {
                        filename = filename + ".bin";
                        File fileToSave = new File(selectedDirectory, filename);
                        appController.saveSheet(fileToSave.getAbsolutePath());
                    }
                }
            }
        } else {
            File file = new File(path.get());
            FileWriter writer = new FileWriter(file);
            appController.saveSheet(path.get());
        }
    }

    @FXML
    public void handleCreateNewSheet() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/component/top/dialog/sheet/createSheetDialog.fxml"));
        Parent root = loader.load();

        SheetDialogController controller = loader.getController();
        controller.setTopController(this);
        controller.setAppController(appController);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Sheet Details");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(root));

        dialogStage.setHeight(240);

        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
    }

    public void EnableButtons(){
        isSheetLoaded.set(true);
    }

    public void setPreviousPath() {
        path.set(previousPath);
    }

    public void setOnMouseCoordinate(CellDto cell) {
        cellId.set(cell.coordinate().toString());
        originalValue.set(cell.originalValue());
        lastUpdate.set(cell.LatestSheetVersionUpdated());
    }

    @FXML
    public void addFilter() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/component/top/dialog/filter/createFilterDialog.fxml"));
        Parent root = loader.load();
        FilterDialogController controller = loader.getController();
        controller.setAppController(appController);
        controller.setBoundaries(appController.getNumOfRows(),appController.getNumOfCols());
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(root));
        dialogStage.setTitle("Filter");
        controller.setDialogStage(dialogStage);
        controller.setAppController(appController);
        dialogStage.showAndWait();
    }

    @FXML
    public void addRangeAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/component/top/dialog/range/createRangeDialog.fxml"));
        Parent root = loader.load();

        RangeDialogController controller = loader.getController();
        controller.setBoundaries(appController.getNumOfRows(),appController.getNumOfCols());
        controller.setAppController(appController);
        controller.setTopController(this);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Range Details");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(root));
        dialogStage.setHeight(240);
        dialogStage.setWidth(350);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
    }

    private void handleRangeSelected(String selectedItem) {
        appController.PaintCells(appController.GetRange(selectedItem).getRangeCellsCoordinate(), "pink");
    }

    public void addRangesToComboBox(List<String> ranges) {
        clearRangeComboBox();
        rangesComboBox.getItems().add("");
        ranges.forEach(rangeName -> rangesComboBox.getItems().add(rangeName));
    }

    private void clearRangeComboBox() {
        rangesComboBox.getItems().clear();
    }

    public void addVersion() {
        SheetVersionComboBox.getItems().add(String.valueOf(SheetVersionComboBox.getItems().size()));
    }

    public void clearVersion() {
        SheetVersionComboBox.getItems().clear();
        SheetVersionComboBox.getItems().add("Version");
    }

    private void handleVersionSelected(String selectedItem) {
        SheetDto sheet = appController.getSheetByVersion(Integer.parseInt(selectedItem));
        createNewSheetInDifferentWindows(sheet);
    }

    public static void createNewSheetInDifferentWindows(SheetDto sheet) {
        GridPane gridPaneSheet = new GridPane();
        GridPane gridPaneLeft = new GridPane();
        GridPane gridPaneTop = new GridPane();


        sheet.cells().forEach((coordinate, cell) -> {
            TextField cellField = new TextField(cell.effectiveValue().toString());
            cellField.setPrefWidth(100);
            cellField.setPrefHeight(30);
            cellField.setId(coordinate.toString());
            gridPaneSheet.add(cellField, coordinate.col(), coordinate.row());
        });

        SheetController.printRowAndColumnsLabels(sheet, gridPaneLeft, gridPaneTop,100,30);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPaneSheet);
        borderPane.setLeft(gridPaneLeft);
        borderPane.setTop(gridPaneTop);

        Stage newStage = new Stage();

        Scene scene = new Scene(borderPane, 800, 600);
        newStage.setScene(scene);

        newStage.show();
    }

    public void addRangeToComboBox(String rangeName){
        rangesComboBox.getItems().add(rangeName);
    }

}

