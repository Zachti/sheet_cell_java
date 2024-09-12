package component.top.dialog.filter;

import cell.Cell;
import component.app.AppController;
import component.top.dialog.range.RangeDialogController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import position.PositionFactory;
import position.interfaces.IPosition;
import range.CellRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static component.top.TopController.createNewSheetInDifferentWindows;

public class FilterDialogController {

    private AppController appController;
    private int numOfRows;
    private int numOfCols;

    @FXML public TextField startPointField;
    @FXML public TextField endPointField;
    @FXML public TextField KeyColumnFiled;
    @FXML public ListView<String> valuesListView;
    @FXML private Stage dialogStage;
    @FXML private VBox errorBox;

    private final Map<String, SimpleBooleanProperty> checked = new HashMap<>();
    // todo - leibo check if it cause errors cause the backend expect object and not string, change accordingly

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public IPosition getStartPoint() {
        return PositionFactory.create(startPointField.getText());
    }

    public IPosition getEndPoint() {
        return PositionFactory.create(endPointField.getText());
    }

    public String getKeyColumn() {
        return KeyColumnFiled.getText();
    }

    private boolean isInputValid() {
        errorBox.getChildren().clear();
        boolean isValid = true;

        if (!PositionFactory.isValidCoordinate(startPointField.getText())) {
            Label errorLabel = new Label("Invalid start point!\n");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
            isValid = false;
        }
        if (!PositionFactory.isValidCoordinate(endPointField.getText())) {
            Label errorLabel = new Label("Invalid end point!\n");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
            isValid = false;
        }

        if (PositionFactory.isValidCoordinate(new String[] { startPointField.getText() , endPointField.getText() } )) {
            if (!RangeDialogController.isRangeValid(startPointField.getText(), endPointField.getText())) {
                Label errorLabel = new Label("Start point should be less than or equal to end point!\n");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorBox.getChildren().add(errorLabel);
                isValid = false;
            }

            if (!RangeDialogController.isInBoundaries(endPointField.getText(), numOfRows, numOfCols)) {
                Label errorLabel = new Label("Range out of boundaries \n");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorBox.getChildren().add(errorLabel);
                isValid = false;
            }

            if(KeyColumnFiled.getText() == null) {
                Label errorLabel = new Label("Key column filed is empty!\n");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorBox.getChildren().add(errorLabel);
                isValid = false;
            }
            else if(!(KeyColumnFiled.getText().length() == 1) || KeyColumnFiled.getText().toUpperCase().charAt(0) < 'A' || KeyColumnFiled.getText().toUpperCase().charAt(0) >= ('A' + numOfCols)){
                Label errorLabel = new Label("Invalid key column\n");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorBox.getChildren().add(errorLabel);
                isValid = false;
            }
        }

        return isValid;
    }

    public void setValuesComboBox(List<String> valuesList) {

        ObservableList<String> observableList = FXCollections.observableArrayList(valuesList);
        valuesListView.setItems(observableList);

        valuesListView.setCellFactory(param -> new ListCell<>() {
            private final Label label = new Label();
            private final CheckBox checkBox = new CheckBox();
            private final HBox hbox = new HBox(label, checkBox);

            {
                hbox.setSpacing(10);
                hbox.setAlignment(Pos.CENTER_LEFT); // Align items to the left
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    SimpleBooleanProperty selected = new SimpleBooleanProperty();
                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> selected.set(newValue));
                    checked.put(item, selected);
                    setGraphic(hbox);
                }
            }
        });
    }

    public void handelOK() {
        if (isInputValid()) {
            keyColumnChanged();
        }
    }

    @FXML
    public void keyColumnChanged() {
        valuesListView.getItems().clear();
        setValuesComboBox(appController.getValuesInColumn(getRange()));
    }

    private CellRange getRange(){
        IPosition startPointCoordinate = getStartPoint();
        IPosition endPointCoordinate = getEndPoint();
        return new CellRange("",startPointCoordinate,endPointCoordinate);
    }

    private int getCol(){
        return getKeyColumn().toLowerCase().charAt(0) - 'a' + 1;
    }

    public void ApplyFilter() {
        List<Object> filters = new ArrayList<>();
        checked.forEach((val, checked) -> {
            if (checked.getValue()) {
                filters.add(val);
            }
        });

        Map<IPosition, Cell> position2Cell = appController.getRowsByFilter(getRange(),filters);
        createNewSheetInDifferentWindows(position2Cell);
    }

    @FXML
    public void handeCancel() {
        dialogStage.close();
    }

    public void setBoundaries(int _numOfRows, int _numOfCols) {
        numOfCols = _numOfCols;
        numOfRows = _numOfRows;
    }
}
