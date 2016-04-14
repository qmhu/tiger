package com.my.mongo.model;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by I311862 on 2016/3/23.
 */
@Document(collection = "EshopMeta")
public class EshopMeta {

    private static final long serialVersionUID = -2568613272795333512L;

    private String domain;

    private String contentPath;

    private String landscape;

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLandscape() {
        return landscape;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
    }
}
