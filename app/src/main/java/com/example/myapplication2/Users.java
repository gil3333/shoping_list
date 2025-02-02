package com.example.myapplication2;

public class Users {
    private String username;
    private String password;
    private String phone;

    public Users() {
        // נדרש עבור Firebase
    }

    public Users(String username, String password, String phone) {
        this.username = username;
        this.password = password;
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }
}


