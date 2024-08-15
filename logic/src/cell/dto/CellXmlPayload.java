package cell.dto;

import position.interfaces.IPosition;
import versionHistory.IVersionHistory;

import java.util.Set;
import java.util.TreeMap;

public record CellXmlPayload(CellDetails cellDetails, IVersionHistory<CellBasicDetails> versionHistory) {

    public IPosition position() { return cellDetails.basicDetails().position(); }

    public TreeMap<Integer, CellBasicDetails> history() { return versionHistory.getStore(); }

    public Set<IPosition> observers() { return cellDetails.observers(); }

    public Set<IPosition> observables() { return cellDetails.observables(); }
}
