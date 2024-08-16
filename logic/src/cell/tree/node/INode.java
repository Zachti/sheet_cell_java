package cell.tree.node;

public interface INode extends Cloneable {
    Object getNodeValue();
    void clear();
    void addChild(INode child);
    boolean isFunction();
    INode clone();
}
