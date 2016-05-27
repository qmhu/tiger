package com.my.mongo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by I311862 on 2016/3/20.
 */
@Document(collection = "OCCStatusDaily")
public class OCCStatusDaily {

    @Id
    private String id;

    private String referEshop;

    private String contentPath;

    private int status;

    private String landscape;

    private int count;

    private Date time;

    public OCCStatusDaily(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferEshop() {
        return referEshop;
    }

    public void setReferEshop(String referEshop) {
        this.referEshop = referEshop;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLandscape() {
        return landscape;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
