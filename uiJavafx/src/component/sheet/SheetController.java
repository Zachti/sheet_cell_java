package component.sheet;

import cell.dto.CellDetails;
import component.sheet.Enum.PropType;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import component.app.AppController;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import jaxb.dto.SheetConfiguration;
import position.Position;
import position.PositionFactory;
import position.interfaces.IPosition;
import range.CellRange;

import java.util.*;

public class SheetController {

    private static final int MIN_ROW_HEIGHT = 20;
    private static final int MIN_COLUMN_WIDTH = 50;
    private static final int MAX_ROW_HEIGHT = 150;
    private static final int MAX_COLUMN_WIDTH = 400;
    private static AppController appController;
    private boolean isSheetLoaded = false;

    @FXML private GridPane gridPaneTop;
    @FXML private GridPane gridPaneLeft;
    @FXML private GridPane gridPaneSheet;
    @FXML public ScrollPane upDownScroller;
    @FXML public ScrollPane rightLeftScroller;

    public final StringProperty defaultCellStyle = new SimpleStringProperty("-fx-border-radius: 5px; -fx-border-color: black; -fx-border-width: 1px;");
    public final StringProperty observersStyle = new SimpleStringProperty( "-fx-border-color: green; -fx-border-width: 2px;");
    public final StringProperty observablesStyle = new SimpleStringProperty( "-fx-border-color: blue; -fx-border-width: 2px;");
    public final StringProperty rangeStyle = new SimpleStringProperty( "-fx-border-color: pink; -fx-border-width: 2px;");
    private final SimpleStringProperty dependsOnColor = new SimpleStringProperty("white");
    private final List<StringProperty> bindToDependsOnColor = new ArrayList<>();
    private final SimpleStringProperty influenceOnColor = new SimpleStringProperty("white");
    private final List<StringProperty> bindToInfluenceOnColor = new ArrayList<>();
    private final SimpleStringProperty colorProp = new SimpleStringProperty("white");
    private final List<StringProperty> bindToColorProp = new ArrayList<>();
    private final Map<IPosition,SimpleStringProperty> sheetData = new HashMap<>();
    private final SimpleIntegerProperty cellWidth = new SimpleIntegerProperty(100);
    private final SimpleIntegerProperty cellHeight = new SimpleIntegerProperty(30);
    private final Map<IPosition, String> previousValues = new HashMap<>();
    private final Set<IPosition> paintedCells = new HashSet<>();
    private final Map<IPosition, String> originalStyles = new HashMap<>();


    public void initialize() {
        upDownScroller.vvalueProperty().addListener((obs, oldVal, newVal) -> gridPaneLeft.setLayoutY(-newVal.doubleValue() * (gridPaneSheet.getHeight() - upDownScroller.getViewportBounds().getHeight())));
        rightLeftScroller.hvalueProperty().addListener((obs, oldVal, newVal) -> gridPaneTop.setLayoutX(Math.max(0, -newVal.doubleValue() * (gridPaneSheet.getWidth() - rightLeftScroller.getViewportBounds().getWidth()))));

    }

    void setColor(SimpleStringProperty prop, String color) {
        String existingStyle = prop.get();
        if (existingStyle == null || existingStyle.trim().isEmpty()) {
            existingStyle = "";
        } else if (!existingStyle.trim().endsWith(";")) {
            existingStyle = existingStyle.trim() + ";";
        }
        String newStyle = existingStyle + "-fx-border-color: " + color + "; -fx-border-width: 2px;";
        prop.set(newStyle);
    }

    public void updateRowAndColumnSizes(int newCellWidth, int newCellHeight) {
        cellWidth.set(newCellWidth);
        cellHeight.set(newCellHeight);

        gridPaneSheet.getChildren().forEach(node -> {
            if (node instanceof TextField) {
                TextField textField = (TextField) node;
                textField.prefWidthProperty().unbind();
                textField.prefHeightProperty().unbind();
                textField.setPrefWidth(newCellWidth);
                textField.setPrefHeight(newCellHeight);
                textField.setMinWidth(MIN_COLUMN_WIDTH);
                textField.setMinHeight(MIN_ROW_HEIGHT);
                textField.setMaxHeight(MAX_ROW_HEIGHT);
                textField.setMaxWidth(MAX_COLUMN_WIDTH);
            }
        });

        gridPaneLeft.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                label.setPrefHeight(newCellHeight);
            }
        });

        gridPaneTop.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                label.setPrefWidth(newCellWidth);
            }
        });
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
            rowLabel.setPrefHeight(height);
            rowLabel.setPrefWidth((double) 30);
            rowLabel.setMinHeight(MIN_ROW_HEIGHT);
            rowLabel.setMaxHeight(MAX_ROW_HEIGHT);
            rowLabel.setAlignment(Pos.CENTER);

            gridPaneLeft.add(rowLabel, 0, row);
            gridPaneLeft.setMargin(rowLabel, new Insets(2));
            GridPane.setHalignment(rowLabel, HPos.CENTER);
            GridPane.setValignment(rowLabel, VPos.CENTER);
        }

        for (int col = 0; col <= configuration.layout().getColumns(); col++) {
            Label colLabel = new Label(Position.getColumnLabel(col));
            colLabel.setPrefWidth(width);
            colLabel.setMinWidth(MIN_COLUMN_WIDTH);
            colLabel.setMaxWidth(MAX_COLUMN_WIDTH);
            if (col == 0) {
                GridPane.setMargin(colLabel, new Insets(2, 2, 2, -2));
            } else {
                GridPane.setMargin(colLabel, new Insets(2, 2, 2,  2));
            }
            gridPaneTop.add(colLabel, col, 0);
            GridPane.setHalignment(colLabel, HPos.CENTER);
            GridPane.setValignment(colLabel, VPos.CENTER);
        }
    }



    public void fillSheet(SheetConfiguration configuration) {

        if (isSheetLoaded) {
            fillExistSheetWithData(configuration);
        } else {
            cellWidth.set(configuration.uiUnits().getColumnWidthUnits());
            cellHeight.set(configuration.uiUnits().getRowsHeightUnits());
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
            cellField.setAlignment(Pos.CENTER);
            sheetData.put(position, new SimpleStringProperty(cell.getEffectiveValue()));
            cellField.textProperty().bindBidirectional(sheetData.get(position));
            cellField.setOnMouseClicked(event -> cellClicked(position));
            cellField.setOnAction(event -> handleCellAction(cellField, position));
            cellField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                appController.getTopComponentController().setCellFocused(isNowFocused);
                if (!isNowFocused) {
                    handleCellAction(cellField, position);
                    removePaint();
                }
            });
            cellField.setStyle(defaultCellStyle.get());

            cellField.setId(position.toString());
            gridPaneSheet.add(cellField, position.column(), position.row());
            gridPaneSheet.setMargin(cellField, new Insets(2));

        });

        printRowAndColumnsLabels(gridPaneLeft, gridPaneTop, cellWidth.get(), cellHeight.get());
    }

    public void Paint(Set<IPosition> positions, String color, PropType propType) {
        SimpleStringProperty styleProp = getProp(propType);
        List<StringProperty> bindingToProp = getBindingsToProp(propType);
        final String styleString;
        if(color == "Green") {
            styleString = observersStyle.get();
        } else if (color == "Blue") {
            styleString = observablesStyle.get();
        }
        else if (color == "Pink"){
            styleString = rangeStyle.get();
        }
        else {
            styleString = defaultCellStyle.get();
        }

        gridPaneSheet.getChildren().forEach(node -> {
            IPosition position = PositionFactory.create(node.getId());
            if (positions.contains(position)) {
                if (!originalStyles.containsKey(position)) {
                    originalStyles.put(position, node.getStyle());
                }
                node.setStyle(styleString);
                bindingToProp.add(node.styleProperty());
                paintedCells.add(position);
            }
        });
    }

    public void alignColumnText(int column, Pos alignment) {
        gridPaneSheet.getChildren().forEach(node -> {
            if (node instanceof TextField && GridPane.getColumnIndex(node) == column) {
                ((TextField) node).setAlignment(alignment);
            }
        });
    }

    public void removePaint() {
        String rangeName = appController.getTopComponentController().selectedRange;
        CellRange selectedRange;
        if(rangeName != null && rangeName != "Default") {
            selectedRange = appController.getRange(rangeName);
        }
        else {
            selectedRange = null;
        }

        gridPaneSheet.getChildren().forEach(node -> {
            IPosition position = PositionFactory.create(node.getId());
            if (node instanceof TextField && paintedCells.contains(position)) {
                node.styleProperty().unbind();
                if (selectedRange != null && rangeName != "Default") {
                    if(selectedRange.contains(position))
                        node.setStyle(rangeStyle.get());
                    else {
                        node.setStyle(originalStyles.getOrDefault(position, defaultCellStyle.get()));
                    }
                } else {
                    node.setStyle(originalStyles.getOrDefault(position, defaultCellStyle.get()));
                }
            }
        });
        paintedCells.clear();
    }

    public void refreshCellDisplay(IPosition position) {
        Node node = getNodeByPosition(position);
        if (node instanceof TextField) {
            TextField cellField = (TextField) node;
            cellField.setText(appController.getTopComponentController().originalValueTextField.getText());
        }
    }

    private void handleCellAction(TextField cellField, IPosition position) {
        String newValue = cellField.getText();
        String oldValue = previousValues.get(position);

        if ((newValue != null && !newValue.isEmpty() && !newValue.equals(oldValue)) ||
                (newValue != null && newValue.isEmpty() && oldValue != null && !oldValue.isEmpty())) {
            try {
                appController.updateCell(position, newValue);
                CellDetails cell = appController.getCellDetailsByPosition(position);
                previousValues.put(position, cell.basicDetails().effectiveValue().toString());
                appController.getTopComponentController().refreshOriginalValueText(cell.basicDetails().originalValue());
            } catch (Exception e) {
                showError(e.getMessage());
            }

            fillSheet(appController.getSheetConfiguration());
        }
    }

    public Node getNodeByPosition(IPosition position) {
        for (Node node : gridPaneSheet.getChildren()) {
            if (node instanceof TextField && node.getId().equals(position.toString())) {
                return node;
            }
        }
        return null;
    }

    public void updateCellStyle(IPosition position, String style) {
        gridPaneSheet.getChildren().forEach(node -> {
            if (node instanceof TextField && node.getId().equals(position.toString())) {
                TextField cellField = (TextField) node;
                cellField.setStyle(style);
                originalStyles.put(position, node.getStyle());
            }
        });
    }

    public Color extractColor(String style, String property) {
        String colorString = extractPropertyValue(style, property);
        if (colorString != null) {
            return Color.web(colorString);
        }
        return Color.WHITE; // Default color if not found
    }

    public String extractTextSize(String style, String property) {
        return extractPropertyValue(style, property);
    }

    private String extractPropertyValue(String style, String property) {
        int startIndex = style.indexOf(property);
        if (startIndex == -1) {
            return null;
        }
        startIndex += property.length() + 1; // Skip property and colon
        int endIndex = style.indexOf(';', startIndex);
        if (endIndex == -1) {
            endIndex = style.length();
        }
        return style.substring(startIndex, endIndex).trim();
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

