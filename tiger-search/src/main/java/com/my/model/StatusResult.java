package com.my.model;

import com.my.elasticsearch.model.Access;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I311862 on 2016/3/21.
 */
public class StatusResult implements Serializable{

    private static final long serialVersionUID = -5568313177795253522L;

    List<AccessStatus> accessStatus = new ArrayList<AccessStatus>();

    public List<AccessStatus> getAccessStatus() {
        return accessStatus;
    }

    public void setAccessStatus(List<AccessStatus> accessStatus) {
        this.accessStatus = accessStatus;
    }
}
