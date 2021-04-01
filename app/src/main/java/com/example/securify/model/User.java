package com.example.securify.model;

import android.net.Uri;

public class User {

    private String name;
    private String userID;
    private String profilePicture;
    private String email;

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

    public String getProfilePicture() {return profilePicture;}

    public String getEmail() {return email;}

    public void setName(String name) {this.name = name;}

    public void setUserID(String userID) {this.userID = userID;}

    public void setProfilePicture(String profilePicture) {this.profilePicture = profilePicture;}

    public void setEmail(String email) {this.email = email;}


}
