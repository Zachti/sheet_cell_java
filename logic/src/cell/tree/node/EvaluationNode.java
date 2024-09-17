package cell.tree.node;

import cell.Cell;
import function.FunctionFactory;
import function.IFunction;
import store.SetContextStore;
import store.TypedContextStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static common.utils.ValueParser.isNumeric;

public final class EvaluationNode implements INode {
    private IFunction function;
    private List<INode> children = new ArrayList<>();
    private Object value;
    private final Cell parent;

    public EvaluationNode(Object value) {
        this.value = value;
        this.parent = null;
    }

    public EvaluationNode(String value, Cell parent) {
        this.value = isNumeric(value) ? handleNumericValue(value) : handleStringValue(value);
        this.parent = parent;
    }

    @Override
    public void addChild(INode child) {
        children.add(child);
    }

    @Override
    public Object getNodeValue() {
        return Optional.ofNullable(function)
                .map(function -> getArgsAndEvaluate())
                .orElse(value);
    }

    private Object getArgsAndEvaluate() {
        TypedContextStore.getSubjectStore().setContext(parent);
        SetContextStore.getCellSetStore().setContext(getNodeParentsContext());
        try {
        List<Object> args = children.stream()
                .map(INode::getNodeValue)
                .toList();
        return function.execute(args);
        } finally {
            TypedContextStore.getSubjectStore().clearContext();
            SetContextStore.getCellSetStore().clearContext();
        }
    }

    private Double handleNumericValue(String value) {
        function = null;
        return Double.parseDouble(value);
    }

    private String handleStringValue(String value) {
        function = FunctionFactory.newInstance(value);
        return value;
    }

    @Override
    public boolean isFunction() { return function != null; }

    @Override
    public void clear() {
        value = "";
        children.clear();
    }

    @Override
    public INode clone() {
        try {
            EvaluationNode clone = (EvaluationNode) super.clone();
            clone.children = new ArrayList<>(children);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Cell> getNodeParentsContext() { return children.stream().filter(child -> isParentPresent((EvaluationNode) child)).map(child -> ((EvaluationNode) child).parent).toList(); }

    private boolean isParentPresent(EvaluationNode node) { return node.parent != null; }
}
