package sheet;

import cache.Cache;
import cache.interfaces.ICache;
import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import common.interfaces.IGenericHandler;
import position.interfaces.IPosition;
import range.IRange;
import sheet.dto.CopySheetDto;
import sheet.dto.CreateSheetDto;
import sheet.history.SheetHistory;
import sheet.interfaces.ISheet;
import sheet.cellManager.CellManager;
import sheet.cellManager.ICellManager;
import store.TypedContextStore;
import versionHistory.IVersionHistory;
import versionHistory.VersionHistory;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public final class Sheet implements ISheet, Cloneable {
    private final String name;
    private int version = 1;
    private ICache<Integer, SheetHistory> versionHistoryCache;
    private Map<Integer,Integer> version2updateCount = new HashMap<>();
    private final ICellManager cellManager;
    private final Map<String, IRange> ranges = new HashMap<>();
    private final IVersionHistory<Map<String, IRange>> rangesHistory;

    public Sheet(CreateSheetDto createSheetDto) {
        name = createSheetDto.name();
        cellManager = new CellManager(createSheetDto.numberOfRows(), createSheetDto.numberOfCols());
        executeWithContext(() -> version2updateCount.put(version, cellManager.initializeCells(createSheetDto.cells())));
        versionHistoryCache = new Cache<>(500, ChronoUnit.MILLIS);
        Map<String, IRange> initialRanges = Objects.requireNonNullElse(createSheetDto.ranges(), new HashMap<>());
        this.rangesHistory = new VersionHistory<>(initialRanges, version);
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
        executeWithContext(() -> updateCellAndVersion(updateCellDto));
    }

    @Override
    public SheetHistory getPastVersion(int version) {
        return executeWithContext(() ->
              versionHistoryCache.getOrElseUpdate(version, () -> this.computePastVersion(version)));
    }

    @Override
    public Map<Integer, Integer> getUpdate2VersionCount() { return version2updateCount; }

    @Override
    public CellBasicDetails getCellBasicDetails(IPosition position) {
        return cellManager.getCellByPosition(position).getBasicDetails();
    }

    @Override
    public CellDetails getCellDetails(IPosition position) {
        return cellManager.getCellByPosition(position).getDetails();
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
    public Map<IPosition, Cell> getCells() { return cellManager.getCells(); }

    @Override
    public Map<IPosition, Cell> getWhatIfCells(List<UpdateCellDto> updateCellDtos) { return cellManager.getWhatIfCells(updateCellDtos); }

    @Override
    public int getVersion() { return version; }

    @Override
    public void addRange(IRange range) {
        if (ranges.containsKey(range.getName())) {
            throw new IllegalArgumentException("Range with name " + range.getName() + " already exists");
        }
        ranges.put(range.getName(), range);
    }

    @Override
    public List<IRange> getRanges() { return ranges.values().stream().toList(); }

    @Override
    public void removeRangeOrThrow(String rangeName) {
        Optional.ofNullable(ranges.get(rangeName))
                .ifPresentOrElse(this::removeRange,
                        () -> {
                    throw new NoSuchElementException("IRange with name '" + rangeName + "' not found in the map.");
                });
    }

    @Override
    public List<Cell> viewCellsInRange(IRange range) {
        return executeWithContext(() -> cellManager.getCellsInRange(range));
    }

    @Override
    public Map<IPosition, Cell> getCellsByFilter(IRange range, List<String> selectedValues) {
        return executeWithContext(() -> cellManager.getCellsByFilter(range, selectedValues));
    }

    @Override
    public List<Integer> sortRowsInRange(IRange range, List<Character> columns, boolean ascending) {
        return executeWithContext(() -> cellManager.sortRowsInRange(range, columns, ascending));
    }

    @Override
    public Map<IPosition, Cell> getRowsByMultiColumnsFilter(IRange range, Map<Character, List<String>> selectedValues, boolean isAnd) {
        return executeWithContext(() -> cellManager.getRowsByMultiColumnsFilter(range, selectedValues, isAnd));
    }

    private SheetHistory computePastVersion(int version) {
        Map<IPosition, Cell> cellHistory = cellManager.computePastVersion(version);
        Map<String, IRange> rangesHistory = this.rangesHistory.getByVersionOrUnder(version);
        return new SheetHistory(cellHistory, rangesHistory);
    }

    private void removeRange(IRange range) {
        if (!range.isValidToDelete()) {
            throw new IllegalArgumentException("Cannot delete range " + range.getName() + " as it is not empty");
        }
        ranges.remove(range.getName());
    }

    private void updateCellAndVersion(UpdateCellDto updateCellDto) {
        Cell cell = cellManager.update(updateCellDto, version + 1);
        version++;
        version2updateCount.put(version, cell.getObserversCount() + 1);
        Map<String, IRange> clonedRanges = ranges.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().clone()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        rangesHistory.addNewVersion(clonedRanges, version);
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
