package cell.effectiveValue;

public interface IEffectiveValue extends Cloneable {
    String getEffectiveValue();
    void setEffectiveValue();
    String getOriginalValue();
    void setOriginalValue(Object originalValue);
    IEffectiveValue clone();
}
