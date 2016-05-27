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

    public DomainViewResult getDomainView(int dayBefore, int limit){
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
                new BasicDBObject("$sort", new BasicDBObject("count", -1)),
                new BasicDBObject("$limit", limit)));

        DomainViewResult domainViewResult = new DomainViewResult();
        for (final DBObject result: outputs.results()){
            System.out.println(result);
            String url = (String) result.get("_id");
            if (url != null) {
                domainViewResult.getDomainViews().add(new DomainView((String) result.get("_id"), String.valueOf(result.get("count")), ""));
            }
        }

        return domainViewResult;
    }


    public PageViewResult getPageView(int dayBefore, int limit) {
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
                new BasicDBObject("$sort", new BasicDBObject("count", -1)),
                new BasicDBObject("$limit", limit)));

        PageViewResult pageViewResult = new PageViewResult();
        for (final DBObject result: outputs.results()){
            System.out.println(result);
            BasicDBObject keys =  (BasicDBObject)result.get("_id");

            if (keys != null) {
                pageViewResult.getPageViews().add(new PageView((String) keys.get("requestUri"), String.valueOf(result.get("count")), ""));
            }
        }

        return pageViewResult;
    }

    public DomainViewResult getPageViewChart(Long start, Long end, String domain, String landscape) {
        String aggregateType = "day";
        Date startDate = null;
        Date endDate = null;
        Calendar cal = Calendar.getInstance();
        if (start != null) {
            cal.setTimeInMillis(start.longValue());
            startDate = cal.getTime();
        }
        if (end != null) {
            cal.setTimeInMillis(end.longValue());
            endDate = cal.getTime();
        }

        BasicDBObject match;

        BasicDBObject matchParams = new BasicDBObject("landscape", "us");
        if (domain != null) {
            matchParams = matchParams.append("httpHost" , domain);
        }

        /*if (landscape != null){
            matchParams = matchParams.append("landscape", landscape);
        }*/

        BasicDBObject timeParams;
        if (startDate != null){
            timeParams = new BasicDBObject("$gte", startDate);
        } else {
            Calendar minCal = Calendar.getInstance();
            minCal.set(Calendar.YEAR, 1970);
            timeParams = new BasicDBObject("$gte", minCal.getTime());
        }

        if (endDate != null){
            timeParams = timeParams.append("$lt", endDate);
        }

        matchParams = matchParams.append("time", timeParams);

        match = new BasicDBObject("$match", matchParams);

        BasicDBObject group = null;
        if (aggregateType.equals("day")){
            group = new BasicDBObject("$group",
                    new BasicDBObject("_id",
                            new BasicDBObject("day",
                                    new BasicDBObject("$dayOfYear","$time")))
                            .append("count", new BasicDBObject("$sum", "$count")));
        }

        BasicDBObject sort = null;
        if (aggregateType.equals("day")){
            sort = new BasicDBObject("$sort", new BasicDBObject("_id.day", 1));
        }else {
            sort = new BasicDBObject("$sort", new BasicDBObject("time", 1));
        }

        List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();
        pipeline.add(match);
        if (group != null){
            pipeline.add(group);
        }
        pipeline.add(sort);
        System.out.println(pipeline);
        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("EshopStatusDaily").aggregate(pipeline);

        DomainViewResult domainViewResult = new DomainViewResult();
        for (final DBObject result: outputs.results()){
            System.out.println(result);

            if (aggregateType.equals("day")){
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

                domainViewResult.getDomainViews().add(new DomainView(domain, String.valueOf(result.get("count")), date.toString()));
            }

        }

        return domainViewResult;
    }
}
