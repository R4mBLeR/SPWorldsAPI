package net.r4mble.types;

public class Item {
    private final String name;
    private final int count;
    private final int price;
    private final String comment;

    public Item(String name, int count, int price, String comment) {
        this.name = name;
        this.count = count;
        this.price = price;
        this.comment = comment;
    }
}
