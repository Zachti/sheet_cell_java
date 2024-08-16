package cell.tree;

import cell.Cell;
import cell.tree.enums.InputSymbols;
import cell.tree.interfaces.ITreeHandler;
import cell.tree.interfaces.ITree;
import cell.tree.node.EvaluationNode;
import cell.tree.node.INode;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

public final class EvaluationTree implements ITree {
    private INode treeRoot;
    private Cell parent;

    public EvaluationTree(Object input) {
        treeRoot = new EvaluationNode(input);
        parent = null;
    }

    public EvaluationTree(Cell parent, String input) {
        this.parent = parent;
        treeRoot = buildTree(input);
    }

    @Override
    public Object evaluate() { return treeRoot.getNodeValue(); }
    
    private INode buildTree(String input) {
        Stack<INode> stack = new Stack<>();
        StringBuilder current = new StringBuilder();
        AtomicReference<INode> root = new AtomicReference<>();
        Map<InputSymbols, ITreeHandler> char2Handler = new EnumMap<>(Map.of(
                InputSymbols.OPEN_BRACE, _ -> {},
                InputSymbols.CLOSE_BRACE, curr -> root.set(handleCloseBrace(stack, curr, root.get())),
                InputSymbols.COMMA, curr -> root.set(handleComma(stack, curr, root.get()))
        ));

        input.trim().chars()
                .mapToObj(c -> (char) c)
                .forEach(c -> InputSymbols.fromChar(c)
                        .ifPresentOrElse(
                                symbol -> handleSymbol(char2Handler.get(symbol), current),
                                () -> current.append(c)
                        )
                );

        return finalizeParsing(stack, root.get(), current);
    }

    private void handleSymbol(ITreeHandler handler, StringBuilder current) {
        handler.handle(current);
        current.setLength(0);
    }

    private INode handleCloseBrace(Stack<INode> stack, StringBuilder current, INode root) {
        INode updatedRoot = handleComma(stack, current, root);
        return Optional.ofNullable(stack.pop()).filter(_ -> stack.isEmpty()).orElse(updatedRoot);
    }

    private INode handleComma(Stack<INode> stack, StringBuilder current, INode root) {
        return Optional.of(current.toString().trim())
                .filter(s -> !s.isEmpty())
                .map(this::stringToNode)
                .map(node -> addChildOrCreateRoot(stack, node, root))
                .orElse(root);
    }

    private INode finalizeParsing(Stack<INode> stack, INode root, StringBuilder current) {
        return Optional.of(current.toString().trim())
                .filter(s -> !s.isEmpty())
                .map(this::stringToNode)
                .map(node -> addChildOrCreateRoot(stack, node, root))
                .orElse(root);
    }

    private INode addChildOrCreateRoot(Stack<INode> stack, INode node, INode root) {
        return Optional.ofNullable(root)
                .map(r -> this.addChildAndReturnRoot(stack, node, r))
                .orElseGet(() -> this.initializeRootNode(stack, node));
    }

    private INode addChildAndReturnRoot(Stack<INode> stack, INode child, INode root) {
        stack.peek().addChild(child);
        if (child.isFunction()) {
            stack.push(child);
        }
        return root;
    }

    private INode stringToNode(String nodeValue) { return new EvaluationNode(nodeValue, parent); }

    private INode initializeRootNode(Stack<INode> stack, INode node) {
        stack.push(node);
        return node;
    }

    @Override
    public void clear() { treeRoot.clear(); }

    @Override
    public ITree clone() {
        try {
            EvaluationTree clone = (EvaluationTree) super.clone();
            clone.treeRoot = treeRoot.clone();
            Optional.ofNullable(parent).ifPresent(_ -> cloneParentCell(clone));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    private void cloneParentCell(EvaluationTree clone) {
         clone.parent = Cell.fromBasicDetails(parent.getBasicDetails());
    } // we cant use parent.clone() as it makes circular clones call
}
