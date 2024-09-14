package component.top;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class CellStyleDialogController {

    @FXML private ColorPicker backgroundColorPicker;
    @FXML private ColorPicker textColorPicker;
    @FXML private TextField textSizeField;

    private TopController topController;
    private String cellId;

    public void setTopController(TopController topController) {
        this.topController = topController;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public void setCurrentValues(Color backgroundColor, Color textColor, String textSize) {
        backgroundColorPicker.setValue(backgroundColor);
        textColorPicker.setValue(textColor);
//        textSizeField.setText(textSize);
    }

    @FXML
    private void handleApply() {
        Color backgroundColor = backgroundColorPicker.getValue();
        Color textColor = textColorPicker.getValue();
//        String textSize = textSizeField.getText();

        topController.applyCellStyle(cellId, backgroundColor, textColor, "12");
        closeDialog();
    }

    @FXML
    private void handleReset() {
        topController.resetCellStyle(cellId);
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) backgroundColorPicker.getScene().getWindow();
        stage.close();
    }
}