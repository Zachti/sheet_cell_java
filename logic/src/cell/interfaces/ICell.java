package cell.interfaces;

import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.CellXmlPayload;
import position.interfaces.IPosition;

public interface ICell extends Cloneable{
    void setOriginalValue(Object originalValue);
    String getOriginalValue();
    Object getEffectiveValue();
    void setEffectiveValue();
    CellBasicDetails getPastVersion(int version);
    IPosition getPosition();
    void update(Object originalValue);
    CellDetails getDetails();
    CellBasicDetails getBasicDetails();
    CellXmlPayload getCellXmlPayload();
    void addNewVersion(int version);
}
