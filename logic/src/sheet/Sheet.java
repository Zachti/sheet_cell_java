package sheet;

import cache.Cache;
import cache.interfaces.ICache;
import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import common.interfaces.IGenericHandler;
import position.interfaces.IPosition;
import sheet.dto.CopySheetDto;
import sheet.dto.CreateSheetDto;
import sheet.interfaces.ISheet;
import sheet.manager.CellManager;
import sheet.manager.ICellManager;
import store.TypedContextStore;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class Sheet implements ISheet {
    private final String name;
    protected int version = 1;
    private ICache<Integer, Map<IPosition, Cell>> versionHistoryCache;
    private Map<Integer,Integer> version2updateCount = new HashMap<>();
    protected final ICellManager cellManager;

    public Sheet(CreateSheetDto createSheetDto) {
        name = createSheetDto.name();
        cellManager = new CellManager(createSheetDto.numberOfRows(), createSheetDto.numberOfCols());
        executeWithContext(() -> version2updateCount.put(version, cellManager.initializeCells(createSheetDto.cells())));
        versionHistoryCache = new Cache<>(500, ChronoUnit.MILLIS);
    }

    public Sheet(CopySheetDto copySheetDto) {
        this(copySheetDto.createSheetDto());
        this.version = copySheetDto.Version();
        version2updateCount.putAll(copySheetDto.version2updateCount());
    }

    @Override
    public final String getName() { return this.name; }

    @Override
    public final void updateCell(IPosition position, String value) {
        executeWithContext(() -> updateCellAndVersion(position, value));
    }

    @Override
    public final Map<IPosition, Cell> getPastVersion(int version) {
        return executeWithContext(() ->
              versionHistoryCache.getOrElseUpdate(version, () -> cellManager.computePastVersion(version)));
    }

    @Override
    public final Map<Integer, Integer> getUpdate2VersionCount() { return version2updateCount; }

    @Override
    public final CellBasicDetails getCellBasicDetails(IPosition position) {
        return cellManager.getCellByPosition(position).getBasicDetails();
    }

    @Override
    public final CellDetails getCellDetails(IPosition position) {
        return cellManager.getCellByPosition(position).getDetails();
    }

    @Override
    public final Sheet clone() {
        try {
            Sheet clone = (Sheet) super.clone();
            clone.versionHistoryCache = new Cache<>(500, ChronoUnit.MILLIS);
            clone.version2updateCount = new HashMap<>(this.version2updateCount);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Sheet clone failed", e);
        }
    }

    @Override
    public final Cell getCellByPosition(IPosition position) { return cellManager.getCellByPosition(position); }

    @Override
    public final void validatePositionOnSheet(IPosition position) { cellManager.validatePositionOnSheet(position); }

    @Override
    public Map<IPosition, Cell> getCells() { return cellManager.getCells(); }

    @Override
    public int getVersion() { return version; }

    private void updateCellAndVersion(IPosition position, String value) {
        Cell cell = cellManager.update(position, value, version + 1);
        version++;
        version2updateCount.put(version, cell.getObserversCount() + 1);
    }

    private <T> T executeWithContext(IGenericHandler<T> handler) {
        TypedContextStore.getSheetStore().setContext(this);
        try {
            return handler.handle();
        } finally {
            TypedContextStore.getSheetStore().clearContext();
        }
    }

    private void executeWithContext(Runnable action) {
        executeWithContext(() -> {
            action.run();
            return null;
        });
    }

}
