package function;

import function.enums.FunctionTypes;

public class FunctionFactory {

    public static IFunction newInstance(String functionName) {
        return FunctionTypes.getFunctionByName(functionName);
    }

    private FunctionFactory() {}
}
