package com.example.demo;

public final class User {
    private static User instance;
    private String name;
    private int userId;

    public User(int id, String name){
        this.userId = id;
        this.name = name;
    }

    public int getUserId(){
        return userId;
    }
    public String getName(){
        return name;
    }
}
