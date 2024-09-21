package sheet.cellManager;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.UpdateCellDto;
import cell.observability.interfaces.ISubject;
import comparator.RowComparator;
import filter.CellFilter;
import filter.IFilter;
import position.interfaces.IPosition;
import range.IRange;
import sheet.builder.SheetBuilder;
import sheet.cellManager.dependencyGraph.DependencyGraph;
import sheet.cellManager.dependencyGraph.IGraph;
import store.SetContextStore;
import store.TypedContextStore;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.utils.InputValidation.validateOrThrow;

public class CellManager implements ICellManager {

    protected Map<IPosition, Cell> position2Cell;
    private final IGraph dependencyGraph;
    private List<Cell> topologicalSort;
    private final IFilter filter;

    public CellManager(int numberOfRows, int numberOfCols) {
        position2Cell= new SheetBuilder(numberOfRows, numberOfCols).build();
        dependencyGraph = new DependencyGraph(position2Cell);
        topologicalSort = dependencyGraph.topologicalSort();
        filter = new CellFilter(position2Cell);
    }

    public CellManager(Map<IPosition, Cell> position2Cell) {
        this.position2Cell = position2Cell;
        dependencyGraph = new DependencyGraph(position2Cell);
        topologicalSort = dependencyGraph.topologicalSort();
        filter = new CellFilter(position2Cell);
    }

    @Override
    public Cell update(UpdateCellDto updateCellDto, int version) {
        Cell cell = getCellOrThrow(updateCellDto.position());
        int toUpdateCellIndex = getUpdateOrder(cell);
        TypedContextStore.getIsObserverUpdateStore().setContext(false);
        try {
            cell.update(updateCellDto.newOriginalValue());
            recalculateCellsFromIndex(toUpdateCellIndex);
        } catch (Exception e) {
            cell.onUpdateFail();
            revertUpdatesFromIndex(toUpdateCellIndex);
            throw e;
        } finally {
            SetContextStore.getSubjectSetStore().clearContext();
            TypedContextStore.getIsObserverUpdateStore().clearContext();
        }
        addNewVersionForUpdatedCells(cell, toUpdateCellIndex, version);
        return cell;
    }

    @Override
    public int initializeCells(List<Cell> cells) {
        List<Cell> insertionOrder = getInsertionOrderOrNull(cells);
        Stream.ofNullable(insertionOrder)
                .flatMap(Collection::stream)
                .forEach(cell -> position2Cell.put(cell.getPosition(), cell));

        dependencyGraph.topologicalSort().forEach(Cell::onSheetInit);

        return Objects.nonNull(insertionOrder) ? insertionOrder.size() : 0;
    }

    @Override
    public Map<IPosition, Cell> computePastVersion(int version) {
        Map<IPosition, CellBasicDetails> pastVersion = new HashMap<>();
        position2Cell.forEach((position, cell) -> pastVersion.put(position, cell.getPastVersion(version)));
        return historicDetailsToHistoricPosition2Cell(pastVersion);
    }

    @Override
    public Cell getCellByPosition(IPosition position) { return getCellOrThrow(position); }

    @Override
    public void validatePositionOnSheet(IPosition position) {
        validateOrThrow(
                position,
                position2Cell::containsKey,
                error -> "Position not on sheet"
        );
    }

    @Override
    public Map<IPosition, Cell> getCells() { return position2Cell; }

    @Override
    public List<Cell> getCellsInRange(IRange range) { return filter.byRange(range); }

    @Override
    public List<Integer> sortRowsInRange(IRange range, List<Character> columns, boolean ascending) {
        return getCellsInRange(range).stream()
                .map(cell -> cell.getPosition().row())
                .sorted(new RowComparator(columns, ascending))
                .collect(Collectors.toList());
    }

    @Override  
    public Map<IPosition, Cell> getWhatIfCells(List<UpdateCellDto> updateCellDtos) {
        Map<IPosition, Cell> whatIfCells = new HashMap<>(position2Cell);
        CellManager whatIfManager = new CellManager(whatIfCells);
        updateCellDtos.forEach(updateCellDto -> whatIfManager.update(updateCellDto, 0));
        return whatIfManager.getCells();
    }

    @Override
    public Map<IPosition, Cell> getCellsByFilter(IRange range, List<String> selectedValues) {
         return filter.ByValues(range, selectedValues);
    }

    @Override
    public Map<IPosition, Cell> getRowsByMultiColumnsFilter(IRange range, Map<Character, List<String>> selectedValues, boolean isAnd) {
        return filter.byMultiColumns(range, selectedValues, isAnd);
    }

    private Map<IPosition, Cell> historicDetailsToHistoricPosition2Cell(Map<IPosition, CellBasicDetails> historicPosition2Cell) {
        Map<IPosition, Cell> position2Cell = new HashMap<>();
        historicPosition2Cell.forEach((position, cellBasicDetails) ->
                position2Cell.put(position, Cell.fromBasicDetails(cellBasicDetails)));
        return position2Cell;
    }

    private Cell getCellOrThrow(IPosition position) {
        return Optional.ofNullable(position2Cell.get(position)).
                orElseThrow(() -> new IllegalArgumentException("Cell not on sheet"));
    }

    private int getUpdateOrder(Cell cell) {
        topologicalSort = dependencyGraph.topologicalSort();
        return topologicalSort.indexOf(cell);
    }

    private void recalculateCellsFromIndex(int startIndex) {
        TypedContextStore.getIsObserverUpdateStore().clearContext();
        TypedContextStore.getIsObserverUpdateStore().setContext(true);
        dependencyGraph.topologicalSort();
        topologicalSort.stream()
                .skip(startIndex + 1)
                .forEach(Cell::setEffectiveValue);
    }

    private void revertUpdatesFromIndex(int startIndex) {
        topologicalSort.stream()
                .skip(startIndex + 1)
                .forEach(Cell::revertUpdate);
    }

    private List<Cell> getInsertionOrderOrNull(List<Cell> cells) {
        return Optional.ofNullable(cells)
                .map(cellList -> cellList.stream()
                        .collect(Collectors.toMap(
                                Cell::getPosition,
                                Function.identity(),
                                (existing, newValue) -> existing,
                                HashMap::new
                        )))
                .map(cellMap -> new DependencyGraph(cellMap).topologicalSort())
                .orElse(null);
    }

    private Set<ISubject> getAllAffectedCells(Cell cell) {
        Set<ISubject> updatedCells = new HashSet<>();
        collectObserversRecursively(cell, updatedCells);
        return updatedCells;
    }

    private void collectObserversRecursively(ISubject cell, Set<ISubject> updatedCells) {
        cell.getObservers().getValues().stream()
                .filter(updatedCells::add)
                .forEach(observer -> collectObserversRecursively(observer, updatedCells));
    }

    private void addNewVersionForUpdatedCells(Cell updatedCell, int toUpdateCellIndex, int version) {
        Set<ISubject> updatedCells = getAllAffectedCells(updatedCell);
        topologicalSort.stream()
                .skip(toUpdateCellIndex)
                .filter(updatedCells::contains)
                .forEach(c -> c.addNewVersion(version));
    }
}
