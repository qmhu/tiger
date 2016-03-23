package com.my.model;

import java.io.Serializable;

/**
 * Created by I311862 on 2016/3/21.
 */
public class ResponseTime implements Serializable {

    private static final long serialVersionUID = -2568683277795933322L;

    private String date;

    private String time;

    public ResponseTime(String date, String time){
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
