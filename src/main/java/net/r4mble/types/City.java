package net.r4mble.types;

public class City {
    private String id;
    private String name;
    private String description;
    private int x;
    private int z;
    private int netherX;
    private int netherZ;
    private Boolean isMayor;

    public City(){
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getNetherX() {
        return netherX;
    }

    public int getNetherZ() {
        return netherZ;
    }
}
