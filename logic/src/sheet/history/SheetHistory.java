package sheet.history;

import cell.Cell;
import position.interfaces.IPosition;
import range.IRange;

import java.util.Map;

public record SheetHistory(Map<IPosition, Cell> position2Cell , Map<String, IRange> ranges) {}
