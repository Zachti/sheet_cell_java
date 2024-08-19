package menu.interfaces;

import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import jaxb.dto.SheetConfiguration;
import menu.enums.MenuAction;
import position.interfaces.IPosition;

import java.io.Closeable;
import java.util.Map;

public interface IMenu extends Closeable {
    MenuAction showMainMenu();
    SheetConfiguration getSheet();
    IPosition getCellPosition();
    void printCellBasicDetails(CellBasicDetails details);
    String getCellNewValue();
    void pleaseTryAgain(Exception e, String message);
    void printCellDetails(CellDetails details);
    int getHistoricVersion(Map<Integer, Integer> version2updateCount);
    String getUserChoice();
    void quit();
}
