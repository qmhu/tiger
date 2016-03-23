package com.my.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I311862 on 2016/3/21.
 */
public class DomainViewResult implements Serializable{

    private static final long serialVersionUID = -2568613177795333322L;

    List<DomainView> domainViews = new ArrayList<DomainView>();

    public List<DomainView> getDomainViews() {
        return domainViews;
    }

    public void setDomainViews(List<DomainView> domainViews) {
        this.domainViews = domainViews;
    }
}
