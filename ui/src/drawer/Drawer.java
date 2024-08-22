package drawer;

public abstract class Drawer implements IDrawer{

    public abstract void draw();

    @Override
    public void draw(String header) {
        display(String.format("%s:", header));
        draw();
    }

    protected String centralizedValue(String value, int length) {
        value = value.length() > length ? value.substring(0, length - 3) + "..." : value;
        int spaces = length - value.length();
        int padLeft = spaces / 2;
        int padRight = spaces - padLeft;
        return String.format("%" + (padLeft + value.length()) + "s", value).concat(" ".repeat(Math.max(padRight, 0)));
    }

    protected void display(String text) { System.out.println(text); }

    protected void display(String format, Object... args) { System.out.printf(format, args); }

}
