package com.my.service;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.my.model.*;
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
@Service("reportService")
public class ReportService {

    @Autowired
    private MongoDBClient mongoDBClient;

    public DomainViewResult getDomainView(int dayBefore){
        dayBefore = -dayBefore;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, dayBefore);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date dateBegin = calendar.getTime();
        System.out.println(dateBegin);

        calendar.add(calendar.DAY_OF_YEAR, 1);
        Date dateEnd = calendar.getTime();
        System.out.println(dateEnd);

        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("EshopAccess").aggregate(asList(
                new BasicDBObject("$match",
                        new BasicDBObject("createTime",
                                new BasicDBObject("$gte", dateBegin).
                                        append("$lt", dateEnd)).
                                append("requestType", "doc")),
                new BasicDBObject("$group",
                        new BasicDBObject("_id","$httpHost").
                        append("count",
                                new BasicDBObject("$sum", 1))),
                new BasicDBObject("$sort", new BasicDBObject("count", -1))));

        DomainViewResult domainViewResult = new DomainViewResult();
        for (final DBObject result: outputs.results()){
            System.out.println(result);
            String url = (String) result.get("_id");
            if (url != null) {
                domainViewResult.getDomainViews().add(new DomainView((String) result.get("_id"), String.valueOf(result.get("count"))));
            }
        }

        return domainViewResult;
    }


    public PageViewResult getPageView(int dayBefore) {
        dayBefore = -dayBefore;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, dayBefore);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date dateBegin = calendar.getTime();
        System.out.println(dateBegin);

        calendar.add(calendar.DAY_OF_YEAR, 1);
        Date dateEnd = calendar.getTime();
        System.out.println(dateEnd);

        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("EshopAccess").aggregate(asList(
                new BasicDBObject("$match",
                        new BasicDBObject("createTime",
                                new BasicDBObject("$gte", dateBegin).
                                        append("$lt", dateEnd)).
                                append("requestType", "doc")),
                new BasicDBObject("$group",
                        new BasicDBObject("_id",
                                new BasicDBObject("requestUri","$requestUri")
                        ).
                         append("count",
                                 new BasicDBObject("$sum", 1))),
                new BasicDBObject("$sort", new BasicDBObject("count", -1))));

        PageViewResult pageViewResult = new PageViewResult();
        for (final DBObject result: outputs.results()){
            System.out.println(result);
            BasicDBObject keys =  (BasicDBObject)result.get("_id");

            if (keys != null) {
                pageViewResult.getPageViews().add(new PageView((String) keys.get("requestUri"), String.valueOf(result.get("count"))));
            }
        }

        return pageViewResult;
    }

    public ResponseTimeResult responseTimeByUrl(String url) {
        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("EshopAccess").aggregate(asList(
                new BasicDBObject("$match",
                        new BasicDBObject("requestType", "doc").append("requestUri" , url)),
                new BasicDBObject("$sort", new BasicDBObject("time", 1))));

        ResponseTimeResult responseTimeResult = new ResponseTimeResult();
        for (final DBObject result: outputs.results()){
            System.out.println(result);
            responseTimeResult.getResponseTimes().add(new ResponseTime(((Date)result.get("time")).toString(), String.valueOf(result.get("responseTime"))));
        }

        return responseTimeResult;
    }

    public ResponseTimeResult responseTimeByDay(String url) {
        BasicDBObject match;
        if (url != null){
            match = new BasicDBObject("$match",
                    new BasicDBObject("requestType", "doc").append("requestUri" , url));
        }else {
            match = new BasicDBObject("$match",
                    new BasicDBObject("requestType", "doc"));
        }

        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("EshopAccess").aggregate(asList(
                match,
                new BasicDBObject("$group",
                        new BasicDBObject("_id",
                                new BasicDBObject("day",
                                        new BasicDBObject("$dayOfYear","$time")))
                        .
                        append("responseTime",
                                new BasicDBObject("$avg", "$responseTime"))),
                new BasicDBObject("$sort", new BasicDBObject("_id.day", 1))));

        ResponseTimeResult responseTimeResult = new ResponseTimeResult();
        for (final DBObject result: outputs.results()){
            System.out.println(result);
            String day = ((BasicDBObject)result.get("_id")).get("day").toString();

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.add(Calendar.DAY_OF_YEAR, Integer.valueOf(day));
            Date date = calendar.getTime();

            responseTimeResult.getResponseTimes().add(new ResponseTime(date.toString(), String.valueOf(result.get("responseTime"))));
        }

        return responseTimeResult;
    }

    public List<String> getDomains() {
        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("EshopMeta").aggregate(asList(
                new BasicDBObject("$group",
                        new BasicDBObject("_id",
                                new BasicDBObject("domain", "$domain")))));

        List<String> domains = new ArrayList<String>();
        for (final DBObject result: outputs.results()){
            System.out.println(result);
            String domain = ((BasicDBObject)result.get("_id")).toString();

            domains.add(domain);
        }

        return domains;

    }

    public List<String> getContentPath(String domain) {
        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("EshopMeta").find(new BasicDBObject("domain", domain));

        List<String> domains = new ArrayList<String>();
        for (final DBObject result: outputs.results()){
            System.out.println(result);
            String domain = ((BasicDBObject)result.get("_id")).toString();

            domains.add(domain);
        }

        return domains;

        return null;
    }
}
