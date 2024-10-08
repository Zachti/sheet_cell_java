package cell;

import cell.dto.*;
import cell.effectiveValue.EffectiveValue;
import cell.effectiveValue.IEffectiveValue;
import cell.interfaces.ICell;
import cell.observability.Subject;
import position.interfaces.IPosition;
import store.SetContextStore;
import store.TypedContextStore;
import versionHistory.IVersionHistory;
import versionHistory.VersionHistory;

public final class Cell extends Subject implements ICell {
    private IVersionHistory<CellBasicDetails> versionHistory;
    private IEffectiveValue effectiveValue;
    private final IPosition position;
    private String style;

    public Cell(CreateCellDto createCellDto) {
        effectiveValue = new EffectiveValue(createCellDto.originalValue(), this);
        position = createCellDto.getPosition();
        versionHistory = new VersionHistory<>(getBasicDetails(), 1);
    }

    public Cell(CopyCellDto copyCellDto) {
        this(copyCellDto.createCellDto());
        versionHistory.copy(copyCellDto.versionHistory());
    }

    public Cell(Cell cell) {
        this(new CopyCellDto(
                new CreateCellDto(cell.getOriginalValue(), cell.position.row(), cell.position.column()),
                cell.observers.keySet(),
                cell.observables.keySet(),
                cell.versionHistory.getStore()
        ));
    }

    private Cell(CellBasicDetails details) {
        effectiveValue = new EffectiveValue(details.originalValue(), details.effectiveValue(), this);
        position = details.position();
    }

    @Override
    public String getStyle() {return style; }

    @Override
    public void setStyle(String style) { this.style = style; }

    @Override
    public IPosition getPosition() { return position; }

    @Override
    public String getOriginalValue() { return effectiveValue.getOriginalValue(); }

    @Override
    public void setOriginalValue(String originalValue) { effectiveValue.setOriginalValue(originalValue); }

    @Override
    public CellBasicDetails getPastVersion(int version) { return versionHistory.getByVersionOrUnder(version); }

    @Override
    public void setEffectiveValue() { effectiveValue.setEffectiveValue(); }

    @Override
    public String getEffectiveValue() { return effectiveValue.getEffectiveValue(); }

    @Override
    public void update(String originalValue) {
        try {
            SetContextStore.getSubjectSetStore().setContext(observables.getValues().stream().toList());
            removeThisFromRangesUser();
            onSubjectUpdate();
            setOriginalValue(originalValue);
        } catch (Exception e) {
            onUpdateFail();
            throw e;
        }
    }

    @Override
    public CellDetails getDetails() {
        return new CellDetails(
                getBasicDetails(),
                versionHistory.getCurrentVersion(),
                observers.keySet(),
                observables.keySet());
    }

    @Override
    public CellBasicDetails getBasicDetails() {
        return new CellBasicDetails(position, getOriginalValue(), getEffectiveValue());
    }

    @Override
    public Cell clone() {
        Cell clone = (Cell) super.clone();
        clone.effectiveValue = effectiveValue.clone();
        return clone;
    }

    @Override
    public CellXmlPayload getCellXmlPayload() { return new CellXmlPayload(getDetails(), versionHistory); }

    @Override
    public void addNewVersion(int version) { versionHistory.addNewVersion(getBasicDetails(), version); }

    @Override
    public void revertUpdate() {
        CellBasicDetails details = getPastVersion(versionHistory.getCurrentVersion());
        effectiveValue = new EffectiveValue(details.originalValue(), details.effectiveValue(), this);
    }

    @Override
    public void onSheetInit() {
        observers.clear();
        setEffectiveValue();
        versionHistory.addNewVersion(getBasicDetails(), versionHistory.getCurrentVersion());
    }

    public static Cell fromBasicDetails(CellBasicDetails details) { return new Cell(details); }

    private void removeThisFromRangesUser() {
        TypedContextStore.getSheetStore().getContext().getRanges().stream()
                .filter(r -> r.getUsers().containsKey(getPosition()))
                .forEach(r -> r.removeUser(getPosition()));
    }
}
