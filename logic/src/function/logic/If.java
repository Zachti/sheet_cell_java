package function.logic;

import cell.Cell;
import cell.observability.interfaces.ISubject;
import function.Function;
import function.enums.NumberOfArgs;
import store.SetContextStore;
import store.TypedContextStore;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class If extends Function<Object> {

    @Override
    public Object execute(List<Object> args) {
        checkNumberOfArgs(args);
        return applyCondition(args);
    }

    private Object applyCondition(List<Object> args) {
        boolean condition = getCondition(args.getFirst().toString());
        Object then = args.get(1);
        Object elze = args.getLast();
        validateReturnType(then, elze);
        setObservers();
        return condition ? then : elze;
    }

    @Override
    protected int getNumberOfArgs() { return NumberOfArgs.THREE.toInt(); }

    private void validateReturnType(Object then, Object elze) {
        if (!then.getClass().equals(elze.getClass())) {
            throw new IllegalArgumentException("return values must be from the same type!");
        }
    }

    private void setObservers() {
        ISubject callingCell = getCallingCell();
        Set<Cell> references = SetContextStore.getNodeParentStore().getContext();
        Optional.ofNullable(references)
                .ifPresent(cells -> cells.forEach(cell -> {
                    cell.addObserver(callingCell);
                    callingCell.addObservable(cell);
                }));
    }

    private ISubject getCallingCell() {
        ISubject callingCell = TypedContextStore.getSubjectStore().getContext();
        return Optional.ofNullable(callingCell)
                .orElseThrow(() -> new IllegalArgumentException("No calling cell context set"));
    }

    private boolean getCondition(String strCondition) {
        if (!strCondition.equalsIgnoreCase("true") && !strCondition.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException("Invalid boolean value: " + strCondition);
        }
        return Boolean.parseBoolean(strCondition);
    }
}
