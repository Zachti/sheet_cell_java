package component.top.dialog.range;

import component.app.AppController;
import component.top.TopController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;

public class RangeDialogController {

    private AppController appController;
    private TopController topController;

    @FXML private TextField rangeNameField;
    @FXML private TextField startPointField;
    @FXML private TextField endPointField;
    @FXML private VBox errorBox;

    private Stage dialogStage;
    private int numOfCols;
    private int numOfRows;

    @FXML private void handleOk() {
        if (isInputValid()) {
            String rangeName = getRangeName();
            Coordinate startCoordinate = getStartPoint();
            Coordinate endCoordinate = getEndPoint();
            appController.addRange(rangeName, startCoordinate, endCoordinate);
            topController.addRangeToComboBox(rangeName);
            dialogStage.close();
        }

    }

    @FXML private void handeCancel(){
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public String getRangeName() {
        return rangeNameField.getText();
    }

    public Coordinate getStartPoint() {
        return CoordinateFactory.getCoordinate(startPointField.getText());
    }

    public Coordinate getEndPoint() {
        return CoordinateFactory.getCoordinate(endPointField.getText());
    }

    private boolean isInputValid() {
        errorBox.getChildren().clear();
        boolean isValid = true;

        if (rangeNameField.getText() == null || rangeNameField.getText().isEmpty()) {
            Label errorLabel = new Label("No valid range name!\n");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
            isValid = false;
        }

        if (!CoordinateFactory.isValidCoordinate(startPointField.getText())) {
            Label errorLabel = new Label("No valid start point!\n");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
            isValid = false;
        }

        if (CoordinateFactory.isValidCoordinate(startPointField.getText()) && CoordinateFactory.isValidCoordinate(endPointField.getText())) {
            if (!isRangeValid(startPointField.getText(), endPointField.getText())) {
                Label errorLabel = new Label("Start point should be less than or equal to end point!\n");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorBox.getChildren().add(errorLabel);
                isValid = false;
            }

            if(!isInBoundaries(endPointField.getText(),numOfRows,numOfCols)) {
                Label errorLabel = new Label("Range out of boundaries \n");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorBox.getChildren().add(errorLabel);
                isValid = false;
            }
        }

        return isValid;
    }

    public static boolean isRangeValid(String startPoint, String endPoint) {
        Coordinate startPointCoordinate = CoordinateFactory.getCoordinate(startPoint);
        Coordinate endPointCoordinate = CoordinateFactory.getCoordinate(endPoint);

        return startPointCoordinate.row() <= endPointCoordinate.row()
                && startPointCoordinate.col() <= endPointCoordinate.col();
    }

    public static boolean isInBoundaries(String endPoint,int numOfRows, int numOfCols){
        Coordinate endPointCoordinate = CoordinateFactory.getCoordinate(endPoint);

        return endPointCoordinate.row() <= numOfRows &&
                endPointCoordinate.col() <= numOfCols;
    }

    public void setBoundaries(int _numOfRows,int _numOfCols){
        numOfCols = _numOfCols;
        numOfRows = _numOfRows;
    }

    public void setAppController(AppController _appController){
        appController = _appController;
    }

    public void setTopController(TopController _topController){
        topController = _topController;
    }

}
