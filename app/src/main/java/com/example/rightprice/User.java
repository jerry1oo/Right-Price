package com.example.rightprice;


public class User {
    public String username;
    public String email;
    public String newmail;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    //Constructor to set user attributes
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.newmail = newmail;
    }
}
