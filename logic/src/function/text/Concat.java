package function.text;

import java.util.List;

public final class Concat extends Formatter {

    @Override
    String format(List<String> strings) {
        return String.join("", strings);
    }
}
