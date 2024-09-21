package cell.validators;

import function.enums.FunctionTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionInputValidator {

    private static final String ARGUMENT_REGEX = "[a-zA-Z0-9_\\-.,/\\\\!@#$%^&*();~?<>\"\\s]+|true|false";
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile(ARGUMENT_REGEX);
    private static final String FUNCTION_REGEX = "[^,{}]+|\\{[^{}]*}|\\{.+}";
    private static final Pattern FUNCTION_PATTERN = Pattern.compile(FUNCTION_REGEX);

    private FunctionInputValidator() {}

    public static boolean isFunctionInput(String input) {
        return input.startsWith("{") && input.endsWith("}");
    }

    public static boolean isValid(String input) {

        List<String> parts = splitFunction(input);

        return FunctionTypes.toList().contains(parts.getFirst().toUpperCase()) && parts.stream().skip(1).allMatch(FunctionInputValidator::isValidArgument);
    }

    private static boolean isValidArgument(String arg) {
        return isFunctionInput(arg)
            ? isValid(arg)
            : ARGUMENT_PATTERN.matcher(arg).matches();
    }

    public static List<String> splitFunction(String input) {
        input = input.substring(1, input.length() - 1);

        Matcher matcher = FUNCTION_PATTERN.matcher(input);
        List<String> result = new ArrayList<>();

        while (matcher.find()) { result.add(matcher.group()); }

        return result;
    }
}
