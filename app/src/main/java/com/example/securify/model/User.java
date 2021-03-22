package com.example.securify.model;

public class User {

    private String name;
    private String userID;

    private static User USER = null;

    public static User getInstance() {

        if(USER == null) {
            USER = new User();
        }

        return USER;
    }

    private User() {}

    public String getName() {return name;}

    public String getUserID() {
        if(userID == null) {
            return "";
        }
        return userID;
    }

    public void setName(String name) {this.name = name;}

    public void setUserID(String userID) {this.userID = userID;}

}
