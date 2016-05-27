package com.my.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I311862 on 2016/3/21.
 */
public class OCCStatusResult implements Serializable{

    private static final long serialVersionUID = -5562313177795253522L;

    List<OCCAccessStatus> accessStatus = new ArrayList<OCCAccessStatus>();

    public List<OCCAccessStatus> getAccessStatus() {
        return accessStatus;
    }

    public void setAccessStatus(List<OCCAccessStatus> accessStatus) {
        this.accessStatus = accessStatus;
    }
}
