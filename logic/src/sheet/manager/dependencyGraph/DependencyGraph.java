package sheet.manager.dependencyGraph;

import cell.Cell;
import position.interfaces.IPosition;

import java.util.*;

public class DependencyGraph implements IGraph {
    public final Map<Cell, Set<IPosition>> dependencyGraph = new HashMap<>();
    private final Map<IPosition, Cell> positionToType;

    public DependencyGraph(Map<IPosition, Cell> position2Cell) {
        this.positionToType = position2Cell;
    }

    private void buildGraph() {
        dependencyGraph.clear();
        positionToType.values().forEach(cell -> buildGraphRecursive(cell, new HashSet<>()));
    }

    private void buildGraphRecursive(Cell cell, Set<IPosition> visited) {
        if (!visited.add(cell.getPosition())) { return; }

        Optional.ofNullable(cell.getObservers())
                .ifPresent(observers -> observers.getValues().forEach(observer -> {
                    dependencyGraph.computeIfAbsent(cell, _ -> new HashSet<>()).add(observer.getPosition());
                    buildGraphRecursive(cell, visited);
                }));

        dependencyGraph.putIfAbsent(cell, new HashSet<>());
    }

    @Override
    public List<Cell> topologicalSort() {
        buildGraph();
        Map<Cell, Boolean> visited = new HashMap<>();
        Map<Cell, Boolean> recursionStack = new HashMap<>();
        Stack<Cell> resultStack = new Stack<>();

        dependencyGraph.keySet().forEach(cell -> visited.put(cell, false));

        try {
            dependencyGraph.keySet().stream()
                    .filter(cell -> !visited.get(cell))
                    .forEach(cell -> performTopologicalSortOrThrow(cell, visited, recursionStack, resultStack));
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Circular dependency detected, this update is illegal!", e);
        }

        return convertStackToReversedList(resultStack);
    }

    private void performTopologicalSortOrThrow(Cell cell, Map<Cell, Boolean> visited, Map<Cell, Boolean> recursionStack, Stack<Cell> resultStack) {
        visited.put(cell, true);
        recursionStack.put(cell, true);

        Optional.ofNullable(dependencyGraph.get(cell))
                .orElse(Collections.emptySet())
                .stream()
                .map(position -> Optional.ofNullable(positionToType.get(position)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(observerCell -> isCyclicOrUnvisited(observerCell, visited, recursionStack, resultStack));

        updateStacks(cell, recursionStack, resultStack);
    }

    private void updateStacks(Cell cell, Map<Cell, Boolean> recursionStack, Stack<Cell> resultStack) {
        recursionStack.put(cell, false);
        resultStack.push(cell);
    }

    private void isCyclicOrUnvisited(Cell observerCell, Map<Cell, Boolean> visited, Map<Cell, Boolean> recursionStack, Stack<Cell> resultStack) {
        if (!visited.getOrDefault(observerCell, false)) {
            performTopologicalSortOrThrow(observerCell, visited, recursionStack, resultStack);
        }
        if (recursionStack.getOrDefault(observerCell, false)) {
            throw new IllegalStateException("Cycle detected at cell: " + observerCell);
        }
    }

    private List<Cell> convertStackToReversedList(Stack<Cell> stack) {
        List<Cell> sortedList = new ArrayList<>(stack.size());
        while (!stack.isEmpty()) { sortedList.add(stack.pop()); }
        return sortedList;
    }
}
