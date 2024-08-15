package cell.effectiveValue;

import cell.Cell;
import cell.enums.CellType;
import cell.tree.EvaluationTree;
import cell.tree.interfaces.ITree;
import cell.validators.FunctionInputValidator;

import static common.utils.InputValidation.validateOrThrow;
import static common.utils.ValueParser.parseValue;

public final class EffectiveValue implements IEffectiveValue {
    private Object effectiveValue;
    private Object originalValue;
    private ITree evaluationTree;
    private CellType type;
    private final Cell parent;

    public EffectiveValue(Object originalValue, Cell Parent) {
        this.type = CellType.fromString(originalValue.toString());
        this.parent = Parent;
        this.originalValue = originalValue;
        evaluationTree = createTree();
        effectiveValue = evaluationTree.evaluate();
    }

    public EffectiveValue(Object originalValue, Object effectiveValue, Cell Parent) {
        this.type = CellType.fromString(originalValue.toString());
        this.parent = Parent;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
    }

    @Override
    public String getEffectiveValue() { return parseValue(effectiveValue.toString()); }

    @Override
    public void setEffectiveValue() {
        evaluationTree = createTree();
        effectiveValue = evaluationTree.evaluate();
    }

    @Override
    public String getOriginalValue() { return parseValue(originalValue.toString()); }

    @Override
    public void setOriginalValue(Object originalValue) {
        this.type = CellType.fromString(originalValue.toString());
        this.originalValue = originalValue;
        setEffectiveValue();
    }

    @Override
    public EffectiveValue clone() {
        Cell parent = Cell.fromBasicDetails(this.parent.getBasicDetails());
        return new EffectiveValue(originalValue, effectiveValue, parent);
    }

    private ITree createTree() {
        Object value = originalValue;
        return type == CellType.FUNCTION ? validateInputAndCreateTree(value.toString()) : new EvaluationTree(value);
    }

    private ITree validateInputAndCreateTree(String input) {
        validateOrThrow(
                input,
                FunctionInputValidator::isValid,
                i -> "Invalid function cell input: " + i
        );

        return new EvaluationTree(parent, input);
    }
}
