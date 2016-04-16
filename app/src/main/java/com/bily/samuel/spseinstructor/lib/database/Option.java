package com.bily.samuel.spseinstructor.lib.database;

/**
 * Created by samuel on 16.4.2016.
 */
public class Option {

    private int id_o;
    private int id_q;
    private String name;
    private int type;

    public Option(){}

    public int getId_o() {
        return id_o;
    }

    public void setId_o(int id_o) {
        this.id_o = id_o;
    }

    public int getId_q() {
        return id_q;
    }

    public void setId_q(int id_q) {
        this.id_q = id_q;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
