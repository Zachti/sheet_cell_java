package manager;

import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import common.interfaces.IHandler;
import drawer.SheetDrawer;
import engine.Engine;
import engine.IEngine;
import jaxb.dto.SheetConfiguration;
import menu.Menu;
import menu.enums.MenuAction;
import menu.interfaces.IMenu;
import position.interfaces.IPosition;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class UiManager implements IUiManager {
    private IMenu menu;
    private IEngine engine;
    private SheetDrawer sheetDrawer;
    private final Map<MenuAction, IHandler> menuAction2Handler = new EnumMap<>(Map.of(
            MenuAction.LOAD, this::handleLoadSheet,
            MenuAction.SHOW_SHEET, this::handleShowSheet,
            MenuAction.SHOW_CELL, this::handleShowCell,
            MenuAction.UPDATE, this::handleUpdateCell,
            MenuAction.SHOW_VERSIONS, this::handleShowHistoricSheetVersions,
            MenuAction.QUIT, this::handleQuit
    ));

    public UiManager() {}

    @Override
    public void run() {
        try(IMenu menu = new Menu()) {
            initialize(menu);
            handleMainMenu();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(IMenu menu) {
        this.menu = menu;
        loadSheet();
    }

    private void loadSheet() {
        SheetConfiguration configuration = menu.getSheet();
        engine = new Engine(configuration.sheet());
        int rows = configuration.layout().getRows();
        int cols = configuration.layout().getColumns();
        sheetDrawer = new SheetDrawer(configuration.uiUnits(), rows, cols, configuration.sheet().getCells());
    }

    private void handleLoadSheet() {
        loadSheet();
        handleShowSheet();
    }

    private void handleShowSheet() {
        String sheetName = engine.getSheetName();
        sheetDrawer.draw(sheetName);
    }

    private void handleShowCell() {
        IPosition position = menu.getCellPosition();
        CellDetails details = engine.getCellDetails(position);
        menu.printCellDetails(details);
    }

    private void handleUpdateCell() {
        IPosition position = menu.getCellPosition();
        CellBasicDetails cell = engine.getCellBasicDetails(position);
        menu.printCellBasicDetails(cell);
        updateCel(position);
    }

    private void handleShowHistoricSheetVersions() {
        Map<Integer, Integer> version2updateCount = engine.getUpdateCountList();
        int version = menu.getHistoricVersion(version2updateCount);
        sheetDrawer.drawHistory(engine.getHistory(version));
    }

    private void updateCel(IPosition position) {
        String newValue = menu.getCellNewValue();
        engine.updateCell(new UpdateCellDto(position, newValue));
    }

    private void handleMenuAction(IHandler handler) {
        String reChoice = null;
        try {
            handler.handle();
        } catch (Exception e) {
            menu.pleaseTryAgain(e, "\nPlease insert valid value.");
            reChoice = menu.getUserChoice();
        } finally {
            getReChoiceStrategy(reChoice, handler);
        }
    }

    private void getReChoiceStrategy(String choice, IHandler handler){
        Optional.ofNullable(choice)
                .filter("y"::equalsIgnoreCase)
                .ifPresentOrElse(handle -> handleMenuAction(handler), this::handleMainMenu);
    }

    private void handleMainMenu() {
        MenuAction action = menu.showMainMenu();
        getAndHandleAction(action);
    }

    private void getAndHandleAction(MenuAction action) {
        IHandler handler = menuAction2Handler.get(action);
        handleMenuAction(handler);
    }

    private void handleQuit() { menu.quit(); }
}
