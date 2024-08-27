package menu;

import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import common.enums.SheetOption;
import common.interfaces.IDescribable;
import drawer.VersionsDrawer;
import jaxb.dto.SheetConfiguration;
import menu.enums.MenuAction;
import menu.interfaces.IMenu;
import position.Position;
import position.interfaces.IPosition;
import xml.IXMLProcessor;
import xml.XMLProcessor;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static common.utils.InputValidation.isInRange;
import static validator.FileValidator.validateFileExists;
import static validator.FileValidator.validateFilePath;

public final class Menu implements IMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final IXMLProcessor xmlProcessor = new XMLProcessor();

    public Menu() { System.out.println("Welcome to Sheet-Cell\n"); }

    @Override
    public MenuAction showMainMenu() {
        return enumMenuToEnumChoice(MenuAction.class, "Please select an action:", MenuAction.size());
    }

    @Override
    public SheetConfiguration getSheet() {
        try {
            SheetOption choice = enumMenuToEnumChoice(SheetOption.class, "Please select an option to load a sheet:", SheetOption.size());
            String filePath =  getFilePath();
            validateFileExists(filePath);
            return xmlProcessor.parse(choice, filePath);
        } catch (Exception e) {
            System.out.println("The file content is invalid. Please provide a valid existing sheet content file.");
            System.out.println(e.getMessage());
            System.out.println("Please try again.");
            return getSheet();
        }
    }

    @Override
    public void printCellBasicDetails(CellBasicDetails details) { System.out.println(details); }

    @Override
    public IPosition getCellPosition() {
        System.out.println("Please enter cell position (e.g. A1): ");
        IPosition position = Position.fromString(scanner.nextLine().toUpperCase());
        return xmlProcessor.validatePosition(position);
    }

    @Override
    public String getCellNewValue() {
        System.out.println("Please enter new value: ");
        return scanner.nextLine().trim();
    }

    @Override
    public void pleaseTryAgain(Exception e, String message) {
        System.out.println(e.getMessage());
        System.out.println(message);
        System.out.println("Press \"y\" to continue or any other key to return to the main menu:");
    }

    @Override
    public void printCellDetails(CellDetails details) { System.out.println(details); }

    @Override
    public void close() {
        Optional.of(isUserWantsToSave())
                .filter(Boolean::booleanValue)
                .ifPresent(save -> saveSheet(getFilePath()));
        scanner.close();
        System.exit(0);
    }

    @Override
    public int getHistoricVersion(Map<Integer, Integer> version2updateCount) {
        new VersionsDrawer(version2updateCount).draw();
        System.out.println("Please enter historic version: ");
        return getValidIntOrThrow(version2updateCount.keySet());
    }

    @Override
    public String getUserChoice() { return scanner.nextLine(); }

    @Override
    public void quit() {
        System.out.println("Goodbye!");
        close();
    }

    private <T extends IDescribable> void enumToMenu(Class<T> enumClass, String openMessage) {
        String menu = Stream.concat(
                        (openMessage != null) ? Stream.of(openMessage) : Stream.empty(),
                        IntStream.range(0, enumClass.getEnumConstants().length)
                                .mapToObj(i -> String.format("%d. %s", i + 1, enumClass.getEnumConstants()[i].getDescription()))
                )
                .collect(Collectors.joining(System.lineSeparator()));

        System.out.println(menu);
    }

    private <T extends IDescribable> int enumMenuToIntChoiceWithValidation(Class<T> enumClass, String message, int max) {
        try {
            enumToMenu(enumClass, message);
            return getValidInteger(max);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return enumMenuToIntChoiceWithValidation(enumClass, message, max);
        }
    }

    private <T extends IDescribable> T enumMenuToEnumChoice(Class<T> enumClass, String message, int max) {
        return enumClass.getEnumConstants()[enumMenuToIntChoiceWithValidation(enumClass, message, max) - 1];
    }

    private int getValidInteger(int maxValue) {
        return Optional.ofNullable(scanner.nextLine())
                .filter(input -> input.matches("\\d+"))
                .map(Integer::parseInt)
                .filter(choice -> isInRange(choice, 1, maxValue))
                .orElseThrow(() -> new IllegalArgumentException("Invalid input, please try again."));
    }

    private int getValidIntOrThrow(Set<Integer> validValues ) {
        int value = getValidInteger(Integer.MAX_VALUE);
        return Optional.of(value).filter(validValues::contains)
                .orElseThrow(() -> new IllegalArgumentException("Invalid value, please try again."));
    }

    private void saveSheet(String filePath) {
        try {
            xmlProcessor.save(filePath);
            System.out.println("\nSheet saved successfully.\n");
        } catch (Exception e) {
            System.out.println("An error occurred while saving the sheet.");
        }
    }

    private boolean isUserWantsToSave() {
        System.out.println("Would you like to save the sheet? (y/n)");
        return "y".equalsIgnoreCase(scanner.nextLine());
    }

    private String getFilePath() {
        System.out.println("Please provide absolute path to the desired location: ");
        return validateFilePath(scanner.nextLine());
    }
}
