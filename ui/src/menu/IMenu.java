package menu;

import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import jaxb.generated.UiSheet;
import menu.enums.MenuAction;
import position.interfaces.IPosition;

import java.io.Closeable;
import java.util.Map;

public interface IMenu extends Closeable {
    MenuAction showMainMenu();
    UiSheet getSheet();
    IPosition getCellPosition();
    void printCellBasicDetails(CellBasicDetails details);
    Object getCellNewValue();
    void pleaseTryAgain(Exception e, String message);
    void printCellDetails(CellDetails details);
    int getHistoricVersion(Map<Integer, Integer> version2updateCount);
    String getUserChoice();
    void quit();
}
