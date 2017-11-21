package com.example.alejandroburdiogarcia.android_exercise.Models;

import java.util.Date;

/**
 * Created by Alejandro Burdío García on 21/11/2017.
 */

public class User {

    public int id;
    public String name;
    public String birthdate;

    public User(int id, String name, String birthdate) {
        this.id=id;
        this.name=name;
        this.birthdate=birthdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

}
