package sheet;

import sheet.interfaces.ISheet;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SheetsManager {
    private static final List<ISheet> SHEETS = new LinkedList<>();

    public static UUID addSheet(ISheet sheet) {
        UUID id = UUID.randomUUID();
        SHEETS.add(sheet.onListInsert(id));
        return id;
    }

    public static void removeSheet(UUID id) { SHEETS.remove(getSheetById(id)); }

    public static ISheet getSheetById(UUID id) {
        return SHEETS.stream().filter(sheet -> sheet.getId().equals(id)).findFirst().orElseThrow();
    }
}
