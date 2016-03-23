package com.my.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I311862 on 2016/3/21.
 */
public class ResponseTimeResult implements Serializable{

    private static final long serialVersionUID = -5568613177795333522L;

    List<ResponseTime> responseTimes = new ArrayList<ResponseTime>();

    public List<ResponseTime> getResponseTimes() {
        return responseTimes;
    }

    public void setResponseTimes(List<ResponseTime> responseTimes) {
        this.responseTimes = responseTimes;
    }
}
