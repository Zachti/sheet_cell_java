package cell.enums;

import cell.validators.FunctionInputValidator;
import common.utils.ValueParser;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public enum CellType {
    TEXT,
    NUMERIC,
    FUNCTION,
    BOOLEAN,
    EMPTY;

    private static final Map<Predicate<String>, CellType> cellTypeMap = Map.of(
            String::isEmpty, EMPTY,
            ValueParser::isNumeric, NUMERIC,
            CellType::isBoolean, BOOLEAN,
            FunctionInputValidator::isFunctionInput, FUNCTION
    );

    private static final Set<String> booleanValues = Set.of(
            Boolean.TRUE.toString(),
            Boolean.FALSE.toString()
    );

    public static CellType fromString(String input) {
        return cellTypeMap.entrySet().stream()
                .filter(entry -> entry.getKey().test(input))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(TEXT);
    }

    public static boolean isBoolean(String input) { return booleanValues.contains(input); }
}
