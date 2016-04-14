package com.my.model;

import java.io.Serializable;

/**
 * Created by I311862 on 2016/3/21.
 */
public class AccessStatus implements Serializable {

    private static final long serialVersionUID = -2568683256795933322L;

    private int statusCode;

    private String requesturi;

    private int count;

    private String date;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getRequesturi() {
        return requesturi;
    }

    public void setRequesturi(String requesturi) {
        this.requesturi = requesturi;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
