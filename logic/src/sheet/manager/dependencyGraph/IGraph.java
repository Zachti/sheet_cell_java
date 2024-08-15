package sheet.manager.dependencyGraph;

import cell.Cell;

import java.util.List;

public interface IGraph {
    List<Cell> topologicalSort();
}
