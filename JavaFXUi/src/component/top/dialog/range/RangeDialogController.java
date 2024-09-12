package component.top.dialog.range;

import component.app.AppController;
import component.top.TopController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import position.PositionFactory;
import position.interfaces.IPosition;

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
            IPosition startCoordinate = getStartPoint();
            IPosition endCoordinate = getEndPoint();
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

    public IPosition getStartPoint() {
        return PositionFactory.create(startPointField.getText());
    }

    public IPosition getEndPoint() {
        return PositionFactory.create(endPointField.getText());
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

        if (!PositionFactory.isValidCoordinate(startPointField.getText())) {
            Label errorLabel = new Label("No valid start point!\n");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
            isValid = false;
        }

        if (PositionFactory.isValidCoordinate(new String[]{startPointField.getText(), endPointField.getText()})) {
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
        IPosition startPointCoordinate = PositionFactory.create(startPoint);
        IPosition endPointCoordinate = PositionFactory.create(endPoint);

        return startPointCoordinate.row() <= endPointCoordinate.row()
                && startPointCoordinate.column() <= endPointCoordinate.column();
    }

    public static boolean isInBoundaries(String endPoint,int numOfRows, int numOfCols){
        IPosition endPointCoordinate = PositionFactory.create(endPoint);

        return endPointCoordinate.row() <= numOfRows &&
                endPointCoordinate.column() <= numOfCols;
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
