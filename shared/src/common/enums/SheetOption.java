package common.enums;

import common.interfaces.IDescribable;

public enum SheetOption implements IDescribable {
    LOAD("Load an existing sheet"),
    NEW("Create a new sheet");

    private final String description;

    SheetOption(String description) { this.description = description; }

    @Override
    public String getDescription() { return description; }

    public static int size() { return values().length; }
}
