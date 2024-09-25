package comparator;

import cell.enums.CellType;
import position.PositionFactory;
import sheet.interfaces.ISheet;
import store.TypedContextStore;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class RowComparator implements Comparator<Integer> {
    private final List<Character> columns;
    private final boolean ascending;
    private static final BiFunction<String, String, Integer> DEFAULT_COMPARATOR = (v1, v2) -> {
        throw new IllegalArgumentException("Unsupported or mismatched cell types");
    };
    private static final Map<CellType, BiFunction<String, String, Integer>> COMPARATORS = Map.of(
            CellType.NUMERIC, RowComparator::compareNumbers,
            CellType.TEXT, RowComparator::compareStrings,
            CellType.BOOLEAN, RowComparator::compareBooleans,
            CellType.EMPTY, RowComparator::compareEmpty
    );

    public RowComparator(List<Character> columns, boolean ascending) {
        this.columns = columns;
        this.ascending = ascending;
    }

    @Override
    public int compare(Integer row1, Integer row2) {
        ISheet sheet = TypedContextStore.getSheetStore().getContext();
        return columns.stream()
                .map(column -> compareRows(sheet, row1, row2, column))
                .filter(comparison -> comparison != 0)
                .findFirst()
                .map(comparison -> ascending ? comparison : -comparison)
                .orElse(0);
    }

    private int compareRows(ISheet sheet, Integer row1, Integer row2, Character column) {
        String value1 = getCellCompareDetails(sheet, row1, column);
        String value2 = getCellCompareDetails(sheet, row2, column);
        return compareByType(CellType.fromString(value1), CellType.fromString(value2), value1, value2);
    }

    private static int compareNumbers(String value1, String value2) {
        Double num1 = parseNumericValue(value1);
        Double num2 = parseNumericValue(value2);
        return num1.compareTo(num2);
    }

    private static int compareStrings(String value1, String value2) { return value1.compareTo(value2); }

    private static int compareBooleans(String value1, String value2) {
        Boolean bool1 = Boolean.parseBoolean(value1);
        Boolean bool2 = Boolean.parseBoolean(value2);
        return bool1.compareTo(bool2);
    }

    private static Double parseNumericValue(String value) {
        return Double.parseDouble(value.replace(",", ""));
    }

    private static int compareEmpty(String value1, String value2) {
        return (value1.isEmpty() && value2.isEmpty()) ? 0 : value1.isEmpty() ? 1 : -1;
    }

    private String getCellCompareDetails(ISheet sheet, Integer row, Character column) {
        return sheet.getCellByPosition(PositionFactory.create(row, column)).getEffectiveValue();
    }

    private int compareByType(CellType type1, CellType type2, String value1, String value2) {
        if (type1 == CellType.EMPTY || type2 == CellType.EMPTY) {
            return compareEmpty(value1, value2);
        }
        return (type1.equals(type2) ? COMPARATORS.get(type1) : DEFAULT_COMPARATOR).apply(value1, value2);
    }
}
