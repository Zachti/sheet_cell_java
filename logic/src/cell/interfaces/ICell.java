package cell.interfaces;

import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.CellXmlPayload;
import position.interfaces.IPosition;

public interface ICell extends Cloneable{
    void setOriginalValue(String originalValue);
    String getOriginalValue();
    Object getEffectiveValue();
    String getStyle();
    void setStyle(String style);
    void setEffectiveValue();
    CellBasicDetails getPastVersion(int version);
    IPosition getPosition();
    void update(String originalValue);
    CellDetails getDetails();
    CellBasicDetails getBasicDetails();
    CellXmlPayload getCellXmlPayload();
    void addNewVersion(int version);
    void onSheetInit();
}
