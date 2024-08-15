package menu.enums;

import menu.IMenuEnum;

public enum MenuAction implements IMenuEnum {
    LOAD("Load a sheet"),
    SHOW_SHEET("Print the sheet"),
    SHOW_CELL("print single cell details"),
    UPDATE("Update a cell"),
    SHOW_VERSIONS("Show sheet historic versions"),
    QUIT("Quit the program");

    private final String description;

    MenuAction(String description) { this.description = description; }

    @Override
    public String getDescription() { return description; }

    public static int size() { return values().length; }
}
