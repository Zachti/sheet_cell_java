package cell.tree.interfaces;

public interface ITree extends Cloneable{
    Object evaluate();
    void clear();
    ITree clone();
}
