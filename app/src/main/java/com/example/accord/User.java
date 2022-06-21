package com.example.accord;

import com.parse.ParseUser;

public class User extends ParseUser {
    public User(){}

    public String getFullName(){
        return getString("fullName");
    }
}
