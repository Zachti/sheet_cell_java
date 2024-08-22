package sheet;

import cache.Cache;
import cache.interfaces.ICache;
import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import common.interfaces.IGenericHandler;
import position.interfaces.IPosition;
import range.CellRange;
import sheet.dto.CopySheetDto;
import sheet.dto.CreateSheetDto;
import sheet.interfaces.ISheet;
import sheet.cellManager.CellManager;
import sheet.cellManager.ICellManager;
import store.TypedContextStore;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class Sheet implements ISheet {
    private UUID id;
    private final String name;
    private int version = 1;
    private ICache<Integer, Map<IPosition, Cell>> versionHistoryCache;
    private Map<Integer,Integer> version2updateCount = new HashMap<>();
    private final ICellManager cellManager;
    private final List<CellRange> ranges = new LinkedList<>();
    private final ReadWriteLock rwl = new ReentrantReadWriteLock();

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
    public String getName() { return this.name; }

    @Override
    public void updateCell(UpdateCellDto updateCellDto) {
        safeWrite(() -> updateCellAndVersion(updateCellDto));
    }

    @Override
    public Map<IPosition, Cell> getPastVersion(int version) {
        return safeRead(() ->
              versionHistoryCache.getOrElseUpdate(version, () -> cellManager.computePastVersion(version)));
    }

    @Override
    public Map<Integer, Integer> getUpdate2VersionCount() { return safeRead(() -> version2updateCount); }

    @Override
    public CellBasicDetails getCellBasicDetails(IPosition position) {
        return safeRead(() -> cellManager.getCellByPosition(position).getBasicDetails());
    }

    @Override
    public CellDetails getCellDetails(IPosition position) {
        return safeRead(() -> cellManager.getCellByPosition(position).getDetails());
    }

    @Override
    public Sheet clone() {
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
    public Cell getCellByPosition(IPosition position) { return cellManager.getCellByPosition(position); }

    @Override
    public void validatePositionOnSheet(IPosition position) { cellManager.validatePositionOnSheet(position); }

    @Override
    public Map<IPosition, Cell> getCells() { return safeRead(cellManager::getCells); }

    @Override
    public Map<IPosition, Cell> getWhatIfCells(List<UpdateCellDto> updateCellDtos) { return safeRead(() -> cellManager.getWhatIfCells(updateCellDtos)); }

    @Override
    public int getVersion() { return version; }

    @Override
    public void addRange(CellRange range) { safeWrite(()-> ranges.add(range)); }

    @Override
    public List<CellRange> getRanges() { return safeRead(() -> ranges); }

    @Override
    public void removeRange(CellRange range) { safeWrite(() -> ranges.remove(range)); }

    @Override
    public List<Cell> viewCellsInRange(CellRange range) {
        return safeRead(() -> cellManager.getCellsInRange(range));
    }

    @Override
    public List<Integer> getRowsByFilter(CellRange range, List<Object> selectedValues) {
        return safeRead(() -> cellManager.getRowsByFilter(range, selectedValues));
    }

    @Override
    public List<Integer> sortRowsInRange(CellRange range, List<Character> columns, boolean ascending) {
        return safeRead(() -> cellManager.sortRowsInRange(range, columns, ascending));
    }

    @Override
    public List<Integer> getRowsByMultiColumnsFilter(CellRange range, List<List<Object>> selectedValues, boolean isAnd) {
        return safeRead(() -> cellManager.getRowsByMultiColumnsFilter(range, selectedValues, isAnd));
    }

    @Override
    public ISheet onListInsert(UUID id) {
        this.id = id;
        return this;
    }

    @Override
    public UUID getId() { return id; }

    private void updateCellAndVersion(UpdateCellDto updateCellDto) {
        Cell cell = cellManager.update(updateCellDto, version + 1);
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

    private <T> T safeRead(IGenericHandler<T> handler) {
        try {
            rwl.readLock().lock();
            return executeWithContext(handler);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            rwl.readLock().unlock();
        }
    }

    private void safeWrite(Runnable action) {
        try {
            rwl.writeLock().lock();
            executeWithContext(action);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            rwl.writeLock().unlock();
        }
    }
}
