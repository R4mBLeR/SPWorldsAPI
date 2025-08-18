package net.r4mble.types;

public class User {
    private String id;
    private String username;
    private String minecraftUUID;
    private String status;
    private String[] roles;
    private City city;
    private Card[] cards;
    private String createdAt;

    public User() {
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getMinecraftUUID() {
        return minecraftUUID;
    }

    public String getStatus() {
        return status;
    }

    public String[] getRoles() {
        return roles;
    }

    public City getCity() {
        return city;
    }

    public Card[] getCards(){
        return cards;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
