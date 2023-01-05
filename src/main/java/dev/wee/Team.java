package dev.wee;

public class Team {
    private String id;
    private String name;
    private String owner_user_id;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwnerUserId() {
        return owner_user_id;
    }

    @Override
    public String toString() {
        return "id: " + id + ", name: " + name + ", owner_user_id: " + owner_user_id;
    }
}