package com.my.model;

import java.io.Serializable;

/**
 * Created by I311862 on 2016/3/21.
 */
public class DomainView implements Serializable {

    private static final long serialVersionUID = -2568613272795333322L;

    private String url;

    private String count;

    public DomainView(String url, String count){
        this.url = url;
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
