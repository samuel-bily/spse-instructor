package com.bily.samuel.spseinstructor.lib.database;

import java.util.Arrays;

/**
 * Created by samuel on 29.2.2016.
 */
public class Question {

    public static final int NONE = 1000;
    public static final int ONE = 0;
    public static final int TWO = 1;
    public static final int THREE = 2;
    public static final int FOUR = 3;

    private int id;
    private String name;
    private String[] options;
    private String stat;
    private int right;

    private int current = NONE;

    public Question(){

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

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":"+"\"" + name + "\"" +
                ",\"0\":"  + "\"" + options[0] + "\"" +
                ",\"1\":"  + "\"" + options[1] + "\"" +
                ",\"2\":"  + "\"" + options[2] + "\"" +
                ",\"3\":"  + "\"" + options[3] + "\"" +
                ",\"ido\":"+ current +
                '}';
    }
}
