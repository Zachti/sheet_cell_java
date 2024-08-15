package function.text;

import function.enums.NumberOfArgs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static common.utils.InputValidation.isInRange;
import static common.utils.InputValidation.validateOrThrow;

public final class Sub extends Formatter {
    @Override
    String format(List<String> strings) {
        String str = strings.get(0);
        int start = parseIntFromString(strings.get(1));
        int end = parseIntFromString(strings.get(2));
        checkIndices(str, start, end);
        return str.substring(start, end);
    }

    @Override
    protected int getNumberOfArgs() {
        return  NumberOfArgs.THREE.toInt();
    }

    private void checkIndices(String str, int index1, int index2) {
        Optional.ofNullable(str)
                .ifPresentOrElse(
                        s -> validateIndices(s, index1, index2),
                        () -> { throw new IllegalArgumentException("String cannot be null"); }
                );
    }

    private void validateIndices(String str, int index1, int index2) {
        int length = str.length();
        validateOrThrow(
                new int[]{index1, index2, length},
                indices -> isInRange(indices[0], 0, indices[1]) && isInRange(indices[1], 0, indices[2] - 1),
                indices -> String.format("Invalid indices: [%d, %d] for string of length %d", indices[0], indices[1], indices[3])
        );
    }

    @Override
    protected List<String> argsToTypeArray(Class<String> type, List<Object> args) {
        return args.stream()
                .map(arg -> args.indexOf(arg) == 0 ? type.cast(arg) : String.valueOf(arg))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private int parseIntFromString(String str) { return (int) Double.parseDouble(str); }
}
