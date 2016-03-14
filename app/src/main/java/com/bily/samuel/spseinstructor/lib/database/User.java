package com.bily.samuel.spseinstructor.lib.database;

/**
 * Created by samuel on 2.2.2016.
 */
public class User {

    private int id;
    private int idu;
    private String name;
    private String email;

    public User(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdu() {
        return idu;
    }

    public void setIdu(int idu) {
        this.idu = idu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
