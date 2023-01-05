package dev.wee;

public class Bot {
    String id;
    String name;
    String token;

    public Bot(String name, String id, String token) {
        this.id = id;
        this.token = token;
        this.name = name;
    }

    @Override
    public String toString() {
        return "id: " + id + ", name: " + name + ", token: " + token;
    }
}
