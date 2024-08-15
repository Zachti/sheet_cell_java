package function.enums;

import function.IFunction;
import function.Math.*;
import function.system.Ref;
import function.text.Concat;
import function.text.Sub;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum FunctionTypes {
    ABS(new Abs()),
    DIVIDE(new Divide()),
    PLUS(new Plus()),
    MINUS(new Minus()),
    TIMES(new Times()),
    MOD(new Mod()),
    POW(new Pow()),
    CONCAT(new Concat()),
    SUB(new Sub()),
    REF(new Ref());

    private final IFunction function;

    private static final Map<String, IFunction> string2Function;

    static {
        string2Function = Arrays.stream(FunctionTypes.values())
                .collect(Collectors.toMap(FunctionTypes::name, type -> type.function));
    }

    FunctionTypes(IFunction function) {
        this.function = function;
    }

    public static IFunction getFunctionByName(String name) {
        return string2Function.get(name);
    }

    public static List<String> toList() {
        return Arrays.stream(FunctionTypes.values())
                .map(Enum::name)
                .toList();
    }
}
