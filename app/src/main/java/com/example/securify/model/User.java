package com.example.securify.model;

public class User {

    private String domainID;

    private String proxyID;

    private String name;

    private String userID;

    public User(String name, String userID) {
        this.name = name;
        this.userID = userID;
    }

    public String getDomainID() {
        return domainID;
    }

    public String getProxyID() {
        return proxyID;
    }

    public String getName() {
        return name;
    }

    public String getUserID() {
        return userID;
    }
}
