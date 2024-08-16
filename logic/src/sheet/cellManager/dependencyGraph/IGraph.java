package sheet.cellManager.dependencyGraph;

import cell.Cell;

import java.util.List;

public interface IGraph {
    List<Cell> topologicalSort();
}
