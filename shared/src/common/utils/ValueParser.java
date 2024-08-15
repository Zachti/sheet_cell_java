package common.utils;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

import static java.lang.Float.parseFloat;

public final class ValueParser {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

    public static String parseValue(String value) {
        return isNumeric(value) ? addSeparator(getNumericValue(parseFloat(value))) : value;
    }

    private static  String getNumericValue(float value) {
        return value % 1 == 0 ?  String.valueOf((int) value) : String.format("%.2f", value);
    }

    private static  String addSeparator(String number) {
        return NUMBER_FORMAT.format(Double.parseDouble(number));
    }

    public static boolean isNumeric(String str) {
        return Optional.ofNullable(str)
                .map(s -> s.matches("-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?"))
                .orElse(false);
    }
}
