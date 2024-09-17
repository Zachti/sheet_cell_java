package function.text;

import common.exception.InvalidIndicesException;
import common.exception.InvalidObserverUpdateException;
import function.Function;

import java.util.List;
import java.util.function.Predicate;

import static common.utils.InputValidation.validateOrThrow;

public abstract class Formatter extends Function<String> {
    private final static String UNDEFINED = "!UNDEFINED!";

    @Override
    public final String execute(List<Object> args) {
        try {
            checkNumberOfArgs(args);
            List<String> strings = argsToTypeArray(String.class, args);
            validateArgsDefined(strings);
            return format(strings);
        } catch (InvalidIndicesException | InvalidObserverUpdateException e ) {
            return UNDEFINED;
        } catch (ClassCastException e) {
            throw new ClassCastException("Function arguments must be of type string!");
        }
    }

    abstract String format(List<String> strings);

    private void validateArgsDefined(List<String> args) {
        Predicate<String> isDefined = arg -> !UNDEFINED.equals(arg);
        args.forEach(arg -> validateOrThrow(arg, isDefined, message -> "Argument is undefined"));
    }
}
