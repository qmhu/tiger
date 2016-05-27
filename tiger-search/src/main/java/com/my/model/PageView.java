package com.my.model;

import java.io.Serializable;

/**
 * Created by I311862 on 2016/3/21.
 */
public class PageView implements Serializable {

    private static final long serialVersionUID = -2568613277795933322L;

    private String url;

    private String time;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String count;

    public PageView(String url, String count, String time){
        this.url = url;
        this.count = count;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
