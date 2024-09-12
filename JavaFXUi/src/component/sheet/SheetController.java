package component.sheet;

import component.sheet.Enum.PropType;
import javafx.animation.PauseTransition;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import component.app.AppController;
import javafx.util.Duration;
import jaxb.dto.SheetConfiguration;
import position.Position;
import position.PositionFactory;
import position.interfaces.IPosition;

import java.util.*;

public class SheetController {

    private static AppController appController;
    private boolean isSheetLoaded = false;

    @FXML private GridPane gridPaneTop;
    @FXML private GridPane gridPaneLeft;
    @FXML private GridPane gridPaneSheet;
    @FXML public ScrollPane upDownScroller;
    @FXML public ScrollPane rightLeftScroller;

    public final StringProperty defaultCellStyle = new TextField().styleProperty();
    private final SimpleStringProperty dependsOnColor = new SimpleStringProperty("white");
    private final List<StringProperty> bindToDependsOnColor = new ArrayList<>();
    private final SimpleStringProperty influenceOnColor = new SimpleStringProperty("white");
    private final List<StringProperty> bindToInfluenceOnColor = new ArrayList<>();
    private final SimpleStringProperty colorProp = new SimpleStringProperty("white");
    private final List<StringProperty> bindToColorProp = new ArrayList<>();
    private final Map<IPosition,SimpleStringProperty> sheetData = new HashMap<>();
    private final SimpleIntegerProperty cellWidth = new SimpleIntegerProperty(100);
    private final SimpleIntegerProperty cellHeight = new SimpleIntegerProperty(30);


    public void initialize() {
        upDownScroller.vvalueProperty().addListener((obs, oldVal, newVal) -> gridPaneLeft.setLayoutY(-newVal.doubleValue() * (gridPaneSheet.getHeight() - upDownScroller.getViewportBounds().getHeight())));
        rightLeftScroller.hvalueProperty().addListener((obs, oldVal, newVal) -> gridPaneTop.setLayoutX(Math.max(0, -newVal.doubleValue() * (gridPaneSheet.getWidth() - rightLeftScroller.getViewportBounds().getWidth()))));
    }

    void setColor(SimpleStringProperty prop,String color) {
        prop.set("-fx-border-color: " + color + "; -fx-border-width: 2px;");
    }

    public void setAppController(AppController appController) {
        SheetController.appController = appController;
    }

    public void clearSheet() {
        isSheetLoaded = false;
        gridPaneSheet.getChildren().clear();
        gridPaneTop.getChildren().clear();
        gridPaneLeft.getChildren().clear();
    }

    public static void printRowAndColumnsLabels(GridPane gridPaneLeft, GridPane gridPaneTop, int width, int height) {
        SheetConfiguration configuration = appController.getSheetConfiguration();
        for (int row = 1; row <= configuration.layout().getRows(); row++) {
            Label rowLabel = new Label(String.valueOf(row));
            rowLabel.setPrefWidth((double) width /2);
            rowLabel.setPrefHeight(height);
            rowLabel.setAlignment(Pos.CENTER);
            gridPaneLeft.add(rowLabel, 0, row);
            GridPane.setHalignment(rowLabel, HPos.CENTER);
            GridPane.setValignment(rowLabel, VPos.CENTER);
        }

        for (int col = 0; col <= configuration.layout().getColumns(); col++) {
            Label colLabel = new Label(Position.getColumnLabel(col));
            colLabel.setPrefWidth(width);
            colLabel.setPrefHeight(height);
            gridPaneTop.add(colLabel, col, 0);
            GridPane.setHalignment(colLabel, HPos.CENTER);
            GridPane.setValignment(colLabel, VPos.CENTER);
        }
    }

    public void fillSheet(SheetConfiguration configuration) {
        cellWidth.set(configuration.uiUnits().getColumnWidthUnits());
        cellHeight.set(configuration.uiUnits().getRowsHeightUnits());
        if (isSheetLoaded) {
            fillExistSheetWithData(configuration);
        } else {
            createNewSheet(configuration);
            isSheetLoaded = true;
        }
    }

    private void fillExistSheetWithData(SheetConfiguration configuration){
        configuration.sheet().getCells().forEach((position, cell) -> sheetData.get(position).set(cell.getEffectiveValue()));
        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(event -> configuration.sheet().getCells().forEach((position, cellDto) -> sheetData.get(position).set(cellDto.getEffectiveValue())));
        pause.play();
    }

    private void createNewSheet(SheetConfiguration configuration) {
        configuration.sheet().getCells().forEach((position, cell) -> {
            TextField cellField = new TextField(cell.getEffectiveValue());
            cellField.prefWidthProperty().bind(cellWidth);
            cellField.prefHeightProperty().bind(cellHeight);
            sheetData.put(position,new SimpleStringProperty(cell.getEffectiveValue()));
            cellField.textProperty().bindBidirectional(sheetData.get(position));
            cellField.setOnMouseClicked(event -> cellClicked(position));
            cellField.setOnAction(event -> handleCellAction(cellField, position));
            cellField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    removePaint();
                }
            });
            cellField.setId(position.toString());
            gridPaneSheet.add(cellField, position.column() , position.row());
        });

        printRowAndColumnsLabels(gridPaneLeft, gridPaneTop,cellWidth.get(),cellHeight.get());
    }

    private void handleCellAction(TextField cellField, IPosition position) {

        try {
            appController.updateCell(position, cellField.getText());
        } catch (Exception e) {
            showError(e.getMessage());
        }

        fillSheet(appController.getSheetConfiguration());
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void cellClicked(IPosition position){
        appController.cellClicked(position);
    }

    public void Paint(Set<IPosition> positions, String color, PropType propType){
        SimpleStringProperty styleProp = getProp(propType);
        List<StringProperty> bindingToProp = getBindingsToProp(propType);
        gridPaneSheet.getChildren().forEach(node -> {
            if(positions.contains(PositionFactory.create(node.getId()))){
                node.styleProperty().bind(styleProp);
                bindingToProp.add(node.styleProperty());
            }
        });

        setColor(styleProp,color);
    }

    public void removePaint(){
        dependsOnColor.set(defaultCellStyle.toString());
        influenceOnColor.set(defaultCellStyle.toString());
        colorProp.set(defaultCellStyle.toString());
        bindToColorProp.forEach(Property::unbind);
        bindToDependsOnColor.forEach(Property::unbind);
        bindToInfluenceOnColor.forEach(Property::unbind);
        bindToDependsOnColor.clear();
        bindToInfluenceOnColor.clear();
        bindToColorProp.clear();

    }

    private SimpleStringProperty getProp(PropType prop){
        return switch (prop) {
            case PropType.INFLUENCE_ON -> influenceOnColor;
            case PropType.DEPENDS_ON -> dependsOnColor;
            case PropType.COLOR -> colorProp;
        };
    }

    private List<StringProperty> getBindingsToProp(PropType prop) {
        return switch (prop) {
            case PropType.INFLUENCE_ON -> bindToInfluenceOnColor;
            case PropType.DEPENDS_ON -> bindToDependsOnColor;
            case PropType.COLOR -> bindToColorProp;
        };
    }
}

