package com.tw0far.potiongames.models;

public class Kit {
    private final int id;
    private final String name;

    public Kit(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
