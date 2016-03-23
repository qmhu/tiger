package com.my.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I311862 on 2016/3/21.
 */
public class PageViewResult implements Serializable{

    private static final long serialVersionUID = -2568613177795333522L;

    List<PageView> pageViews = new ArrayList<PageView>();

    public List<PageView> getPageViews() {
        return pageViews;
    }

    public void setPageViews(List<PageView> pageViews) {
        this.pageViews = pageViews;
    }
}
