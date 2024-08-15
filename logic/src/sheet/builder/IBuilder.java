package sheet.builder;

import cell.Cell;
import position.interfaces.IPosition;

import java.util.LinkedHashMap;

public interface IBuilder extends Cloneable{
    LinkedHashMap<IPosition, Cell> build();
    int getNumberOfRows();
    int getNumberOfCols();
}
