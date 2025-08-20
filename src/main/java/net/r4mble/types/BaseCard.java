package net.r4mble.types;

public class BaseCard {
    protected String id;
    protected String name;
    private String number;
    protected int color;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public int getColor() {
        return color;
    }
}
