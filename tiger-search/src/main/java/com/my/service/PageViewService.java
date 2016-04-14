package com.my.service;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.my.model.*;
import com.my.util.Util;
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
@Service("pageViewService")
public class PageViewService {

    @Autowired
    private MongoDBClient mongoDBClient;

    public DomainViewResult getDomainView(int dayBefore){
        dayBefore = -dayBefore;

        Date dateBegin = Util.getBeginDateForDay(dayBefore);
        System.out.println(dateBegin);

        Date dateEnd = Util.getEndDateForDay(dayBefore);
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

        Date dateBegin = Util.getBeginDateForDay(dayBefore);
        System.out.println(dateBegin);

        Date dateEnd = Util.getEndDateForDay(dayBefore);
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

}
