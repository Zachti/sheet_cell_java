package drawer;

import java.util.Map;

public final class VersionsDrawer extends Drawer {
    private final Map<Integer, Integer> version2UpdateCount;
    private static final int FIRST_COL_WIDTH = 10;
    private static final int SECOND_COL_WIDTH = 14;
    private static final int BORDER_LENGTH = FIRST_COL_WIDTH + SECOND_COL_WIDTH + 5;
    private static final String FORMAT = "%-" + FIRST_COL_WIDTH + "s | %-" + SECOND_COL_WIDTH + "s%n";

    public VersionsDrawer(Map<Integer, Integer> version2UpdateCount) {
        this.version2UpdateCount = version2UpdateCount;
    }

    @Override
    public void draw() {
        display(FORMAT,
                centralizedValue("Version", FIRST_COL_WIDTH),
                centralizedValue("Update Count", SECOND_COL_WIDTH));

       display("-".repeat(BORDER_LENGTH));

        version2UpdateCount.forEach(this::drawRow);
    }

    private void drawRow(Integer version, Integer updateCount) {
        display(FORMAT,
                centralizedValue(String.valueOf(version), FIRST_COL_WIDTH),
                centralizedValue(String.valueOf(updateCount), SECOND_COL_WIDTH));
    }
}
