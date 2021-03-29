package com.e.easearch.model;

public class User {

    private String uid, name, surname, username, password;


    public User() {
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return this.getName();
    }

    public boolean isNull(){
        if(getName().equals("")||getSurname().equals("")||getUsername().equals("")||getPassword().equals("")){
            return false;
        } else {
            return true;
        }
    }
}
