package function.system;

import cell.Cell;
import cell.observability.interfaces.ISubject;
import function.Function;
import function.enums.NumberOfArgs;
import position.PositionFactory;
import position.interfaces.IPosition;
import sheet.interfaces.ISheet;
import store.SetContextStore;
import store.TypedContextStore;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static common.utils.ValueParser.isNumeric;

public final class Ref extends Function<Object> {

    @Override
    public Object execute(List<Object> args) {
        checkNumberOfArgs(args);
        Cell referencedCell = validateAndSetObservers(args.getFirst());
        String value = referencedCell.getEffectiveValue().replace(",", "");
        return isNumeric(value) ? Double.parseDouble(value) : value;
    }

    @Override
    protected int getNumberOfArgs() { return  NumberOfArgs.ONE.toInt(); }

    private Cell validateAndSetObservers(Object arg) {
        Cell referencedCell = argToCell(arg);
        ISubject callingCell = getCallingCell();
        referencedCell.addObserver(callingCell);
        callingCell.addObservable(referencedCell);
        return referencedCell;
    }

    private ISubject getCallingCell() {
        ISubject callingCell = TypedContextStore.getSubjectStore().getContext();
        return Optional.ofNullable(callingCell)
                .orElseThrow(() -> new IllegalArgumentException("No calling cell context set"));
    }

    private Cell argToCell(Object arg) {
        IPosition position = PositionFactory.create(((String) arg).toUpperCase());
        return searchCellInStores(position);
    }

    private Cell searchCellInStores(IPosition position) {
        return Optional.ofNullable(TypedContextStore.getSheetStore().getContext())
                .map(sheet -> searchInSheetStore(sheet, position))
                .orElseGet(() -> searchInCellsStore(position));
    }

    private Cell searchInSheetStore(ISheet sheet, IPosition toSearch) {
        sheet.validatePositionOnSheet(toSearch);
        return sheet.getCellByPosition(toSearch);
    }

        private Cell searchInCellsStore(IPosition toSearch) {
        Set<Cell> cells = SetContextStore.getCellSetStore().getContext();
        return cells.stream().filter(cell -> cell.getPosition().equals(toSearch))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No cell found at position " + toSearch));
    }
}
