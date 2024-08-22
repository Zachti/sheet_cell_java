package function.enums;

public enum NumberOfArgs {
    ONE(1),
    TWO(2),
    THREE(3);

    private final int numArgs;

    NumberOfArgs(int numArgs) {
        this.numArgs = numArgs;
    }

    public int toInt() {
        return numArgs;
    }
}
