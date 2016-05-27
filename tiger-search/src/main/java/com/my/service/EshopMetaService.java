package com.my.service;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.my.model.*;
import com.my.mongo.model.EshopMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by I311862 on 2016/3/21.
 */
@Service("eshopMetaService")
public class EshopMetaService {

    @Autowired
    private MongoDBClient mongoDBClient;

    public List<String> getDomains(String landscape) {
        List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();
        if (landscape != null){
            pipeline.add(new BasicDBObject("$match", new BasicDBObject("landscape", landscape)));
        }
        pipeline.add(new BasicDBObject("$group",
                new BasicDBObject("_id", new BasicDBObject("domain", "$domain"))));


        System.out.println(pipeline);

        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("EshopMeta").aggregate(pipeline);

        List<String> domains = new ArrayList<String>();
        for (final DBObject result: outputs.results()){
            System.out.println(result);
            String domain = ((BasicDBObject)result.get("_id")).get("domain").toString();

            domains.add(domain);
        }

        return domains;

    }

    public List<String> getContentPath(String domain, String landscape, int limit) {
        BasicDBObject queryParams = new BasicDBObject("domain", domain);
        if (landscape != null){
            queryParams.append("landscape", landscape);
        }

        DBCursor dbCursor = mongoDBClient.getMongoOperation().getCollection("EshopMeta").find(queryParams).limit(limit);

        List<String> contents = new ArrayList<String>();
        while (dbCursor.hasNext()){
            DBObject curr = dbCursor.next();
            System.out.println(curr);
            contents.add((String)curr.get("contentPath"));
        }

        return contents;
    }

    public void generateMeta() {
        // delete all meta first
        mongoDBClient.
                getMongoOperation().
                getCollection("EshopMeta").remove(
                new BasicDBObject("",""));

        // rebuild EshopMeta meta
        AggregationOutput outputPath = mongoDBClient.getMongoOperation().getCollection("EshopStatusDaily").aggregate(asList(
                new BasicDBObject("$group",
                        new BasicDBObject("_id", new BasicDBObject("httpHost", "$httpHost").
                                append("contentPath", "$contentPath").append("landscape", "$landscape")))));

        for (final DBObject result: outputPath.results()){
            System.out.println(result);
            BasicDBObject meta = (BasicDBObject) result.get("_id");
            if (meta != null) {
                EshopMeta eshopMeta = new EshopMeta();
                eshopMeta.setContentPath((String)meta.get("contentPath"));
                eshopMeta.setDomain((String)meta.get("httpHost"));
                eshopMeta.setLandscape((String)meta.get("landscape"));
                mongoDBClient.getMongoOperation().save(eshopMeta);
            }
        }
    }

}
