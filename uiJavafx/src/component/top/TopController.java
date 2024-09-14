package component.top;

import cell.Cell;
import cell.dto.CellDetails;
import component.app.AppController;
import component.sheet.SheetController;
import component.top.dialog.filter.FilterDialogController;
import component.top.dialog.sheet.SheetDialogController;
import component.top.dialog.range.RangeDialogController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import position.PositionFactory;
import position.interfaces.IPosition;
import range.CellRange;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TopController {

    private AppController appController;

    private static final int MIN_ROW_HEIGHT = 20;
    private static final int MIN_COLUMN_WIDTH = 50;
    private static final int MAX_ROW_HEIGHT = 150;
    private static final int MAX_COLUMN_WIDTH = 400;
    private final SimpleIntegerProperty cellWidth = new SimpleIntegerProperty(100);
    private final SimpleIntegerProperty cellHeight = new SimpleIntegerProperty(30);
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
    @FXML
    private Button IncreaseRow;
    @FXML
    private Button DecreaseRow;
    @FXML
    private Button IncreaseColumn;
    @FXML
    private Button DecreaseColumn;
    @FXML
    private TextField rowHeightTextField;
    @FXML
    private TextField columnWidthTextField;
    @FXML
    private Button openStyleDialogButton;

    private final SimpleBooleanProperty isSheetLoaded = new SimpleBooleanProperty(false);
    private final SimpleStringProperty originalValue = new SimpleStringProperty("");
    private final SimpleIntegerProperty lastUpdate = new SimpleIntegerProperty(0);
    private final SimpleStringProperty cellId = new SimpleStringProperty("");
    private final SimpleStringProperty path = new SimpleStringProperty("");
    private BooleanProperty isCellFocused = new SimpleBooleanProperty(false);

    @FXML
    public void initialize() {
        rowHeightTextField.setText("30");
        columnWidthTextField.setText("100");
        pathTextField.textProperty().bind(path);
        originalValueTextField.textProperty().bind(originalValue);
        lastUpdateTextField.textProperty().bind(Bindings.format("%d", lastUpdate));
        cellIdTextField.textProperty().bind(cellId);
        saveButton.disableProperty().bind(isSheetLoaded.not());
        rowHeightTextField.disableProperty().bind(isSheetLoaded.not());
        columnWidthTextField.disableProperty().bind(isSheetLoaded.not());
        SheetVersionComboBox.disableProperty().bind(isSheetLoaded.not());
        rangesComboBox.disableProperty().bind(isSheetLoaded.not());
        openStyleDialogButton.disableProperty().bind(isCellFocused.not());

        rangesComboBox.setOnAction(event -> {
            String selectedValue = (String) rangesComboBox.getValue();
            if (selectedValue == null || selectedValue.isEmpty()) {
                appController.removePaint();
            } else {
                appController.removePaint();
                handleRangeSelected(selectedValue);
            }
        });


        rowHeightTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String previousValue = rowHeightTextField.getText();
                int newHeight = Integer.parseInt(rowHeightTextField.getText());
                if (newHeight >= MIN_ROW_HEIGHT && newHeight <= MAX_ROW_HEIGHT) {
                    appController.getSheetComponentController().updateRowAndColumnSizes(cellWidth.get(), newHeight);
                    cellHeight.set(newHeight);
                }
                else {
                    rowHeightTextField.setText(previousValue);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Please set a value between 20 and 150");
                    Platform.runLater(alert::showAndWait);
                }
            }
        });

        columnWidthTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String previousValue = columnWidthTextField.getText();
                int newWidth = Integer.parseInt(columnWidthTextField.getText());
                if (newWidth >= MIN_COLUMN_WIDTH && newWidth <= MAX_COLUMN_WIDTH) {
                    appController.getSheetComponentController().updateRowAndColumnSizes(newWidth, cellHeight.get());
                    cellWidth.set(newWidth);
                } else {
                    columnWidthTextField.setText(previousValue);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Please set a value between 50 and 400");
                    Platform.runLater(alert::showAndWait);
                }
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
            String selectedValue = (String) SheetVersionComboBox.getValue();
            if (selectedValue != null && !selectedValue.equals("Version")) {
                handleVersionSelected(selectedValue);
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
    private void handleOpenStyleDialog() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/component/top/dialog/cellStyles/cellStyleDialog.fxml"));
        Parent root = loader.load();

        component.top.CellStyleDialogController styleController = loader.getController();
        styleController.setTopController(this);
        styleController.setCellId(cellIdTextField.getText());

        IPosition position = PositionFactory.create(cellIdTextField.getText());
        TextField cellField = (TextField) appController.getSheetComponentController().getNodeByPosition(position);
        String style = cellField.getStyle();

        Color backgroundColor = appController.getSheetComponentController().extractColor(style, "-fx-background-color");
        Color textColor = appController.getSheetComponentController().extractColor(style, "-fx-text-fill");
        String textSize = appController.getSheetComponentController().extractTextSize(style, "-fx-font-size");

        styleController.setCurrentValues(backgroundColor, textColor, textSize);

        Stage stage = new Stage();
        stage.setTitle(cellIdTextField.getText() + " Cell Style");
        stage.setScene(new Scene(root));
        stage.setWidth(250);
        stage.setHeight(300);
        stage.show();
    }


    public void applyCellStyle(String cellId, Color backgroundColor, Color textColor, String textSize) {
        IPosition position = PositionFactory.create(cellId);
        TextField cellField = (TextField) appController.getSheetComponentController().getNodeByPosition(position);
        String existingStyle = cellField.getStyle();

        // Update background color
        String updatedStyle = updateStyleProperty(existingStyle, "-fx-background-color", String.format("#%02x%02x%02x",
                (int) (backgroundColor.getRed() * 255),
                (int) (backgroundColor.getGreen() * 255),
                (int) (backgroundColor.getBlue() * 255)));

        // Update text color
        updatedStyle = updateStyleProperty(updatedStyle, "-fx-text-fill", String.format("#%02x%02x%02x",
                (int) (textColor.getRed() * 255),
                (int) (textColor.getGreen() * 255),
                (int) (textColor.getBlue() * 255)));

        updatedStyle = updateStyleProperty(updatedStyle, "-fx-border-radius", "5px");
        updatedStyle = updateStyleProperty(updatedStyle, "-fx-border-color", "black");
        updatedStyle = updateStyleProperty(updatedStyle, "-fx-border-width", "1px");

        // Update text size
        if (textSize != null && !textSize.isEmpty()) {
            updatedStyle = updateStyleProperty(updatedStyle, "-fx-font-size", textSize + "px");
        }

        cellField.setStyle(updatedStyle);
        appController.getSheetComponentController().updateCellStyle(position, updatedStyle);
    }

    private String updateStyleProperty(String style, String property, String value) {
        String updatedStyle;
        if (style.contains(property)) {
            updatedStyle = style.replaceAll(property + ":\\s*[^;]+;", property + ": " + value + ";");
        } else {
            updatedStyle = style + " " + property + ": " + value + ";";
        }
        return updatedStyle;
    }

    public void resetCellStyle(String cellId) {
        IPosition position = PositionFactory.create(cellId);
        TextField cellField = (TextField) appController.getSheetComponentController().getNodeByPosition(position);

        String initialStyle = "-fx-border-radius: 5px; " +
                "-fx-border-color: black; " +
                "-fx-border-width: 1px;";

        cellField.setStyle(initialStyle);
        appController.getSheetComponentController().updateCellStyle(position, initialStyle);
    }



    @FXML
    void HandleSaveFile() throws Exception {
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
                        filename = filename + ".xml";
                        File fileToSave = new File(selectedDirectory, filename);
                        appController.saveSheet(fileToSave.getAbsolutePath());
                    }
                }
            }
        } else {
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

    public void setCellFocused(boolean focused) {
        isCellFocused.set(focused);
    }

    public void setOnMouseCoordinate(CellDetails cell) {
        cellId.set(cell.basicDetails().position().toString());
        originalValue.set(cell.originalValue().toString());
        lastUpdate.set(cell.currentVersion());
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
        dialogStage.setHeight(280);
        dialogStage.setWidth(350);
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
        dialogStage.setHeight(280);
        dialogStage.setWidth(350);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
    }

    private void handleRangeSelected(String selectedItem) {
        CellRange selectedRange = appController.getRange(selectedItem);
        Set<IPosition> toPrint = this.appController.getSheetConfiguration().sheet().getCells().values().stream().map(Cell::getPosition).filter(selectedRange::contains).collect(Collectors.toSet());
        appController.PaintCells(toPrint, "pink");
        // todo - leibo figure out what is selectedItem , do he meant to the range name ? cause that what i filtered , i cant understand it.
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
        Map<IPosition, Cell> position2Cell = appController.getSheetByVersion(Integer.parseInt(selectedItem));
        createNewSheetInDifferentWindows(position2Cell);
    }

    public static void createNewSheetInDifferentWindows(Map<IPosition, Cell> position2Cell) {
        GridPane gridPaneSheet = new GridPane();
        GridPane gridPaneLeft = new GridPane();
        GridPane gridPaneTop = new GridPane();


        position2Cell.forEach((position, cell) -> {
            TextField cellField = new TextField(cell.getEffectiveValue());
            cellField.setEditable(false);
            cellField.setFocusTraversable(false);
            cellField.setMouseTransparent(true);
            cellField.setPrefWidth(100);
            cellField.setPrefHeight(30);
            cellField.setId(position.toString());
            gridPaneSheet.add(cellField, position.column(), position.row());
            gridPaneSheet.setMargin(cellField, new Insets(2));
        });

        SheetController.printRowAndColumnsLabels(gridPaneLeft, gridPaneTop,100,30);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPaneSheet);
        borderPane.setLeft(gridPaneLeft);
        borderPane.setTop(gridPaneTop);

        Stage newStage = new Stage();

        Scene scene = new Scene(borderPane, 1100, 800);
        newStage.setScene(scene);

        newStage.show();
    }

    public void addRangeToComboBox(String rangeName){
        rangesComboBox.getItems().add(rangeName);
    }

}

