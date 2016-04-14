package com.my.service;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.my.model.*;
import com.my.mongo.model.EshopStatusDaily;
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
@Service("statusService")
public class StatusService {

    @Autowired
    private MongoDBClient mongoDBClient;

    public ResponseTimeResult responseTime(String domain, String contentPath, Long start, Long end, String aggregateType) {
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

        BasicDBObject matchParams = new BasicDBObject("requestType", "doc").append("status", 200);
        if (domain != null) {
            matchParams = matchParams.append("httpHost" , domain);
        }

        if (contentPath != null){
            matchParams = matchParams.append("contentPath", contentPath);
        }

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

        matchParams = matchParams.append("createTime", timeParams);

        match = new BasicDBObject("$match", matchParams);
        System.out.println(match.toString());

        BasicDBObject group = null;
        if (aggregateType.equals("day")){
            group = new BasicDBObject("$group",
                    new BasicDBObject("_id",
                            new BasicDBObject("day",
                                    new BasicDBObject("$dayOfYear","$time")))
                            .
                            append("responseTime",
                                    new BasicDBObject("$avg", "$responseTime")));
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
        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("EshopAccess").aggregate(pipeline);

        ResponseTimeResult responseTimeResult = new ResponseTimeResult();
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

                responseTimeResult.getResponseTimes().add(new ResponseTime(date.toString(), String.valueOf(result.get("responseTime"))));
            }else {
                responseTimeResult.getResponseTimes().add(new ResponseTime(result.get("time").toString(), String.valueOf(result.get("responseTime"))));
            }

        }

        return responseTimeResult;
    }

    public void deleteDailyStatus(int dayBefore, String landscape){
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

        WriteResult result = mongoDBClient.
                getMongoOperation().
                getCollection("EshopStatusDaily").remove(
                new BasicDBObject("createTime",new BasicDBObject("$gte", dateBegin).append("$lt", dateEnd)).
                        append("landscape", landscape));
        System.out.println(result);
    }

    public void generateDailyStatus(int dayBefore, String landscape) {
        dayBefore = -dayBefore;

        Date dateBegin = Util.getBeginDateForDay(dayBefore);
        System.out.println(dateBegin);

        Date dateEnd = Util.getEndDateForDay(dayBefore);
        System.out.println(dateEnd);

        BasicDBObject timeParams = new BasicDBObject("$gte", dateBegin).append("$lt", dateEnd);

        AggregationOutput outputPath = mongoDBClient.getMongoOperation().getCollection("EshopAccess").aggregate(asList(
                new BasicDBObject("$match",
                        new BasicDBObject("requestType","doc").
                                append("status", new BasicDBObject("$gte", 400)).
                                append("time", timeParams)),
                new BasicDBObject("$group",
                        new BasicDBObject("_id", new BasicDBObject("httpHost", "$httpHost").
                                append("contentPath", "$contentPath").
                                append("landscape", "$landscape").
                                append("status", "$status")).
                                append("count", new BasicDBObject("$sum", 1)))));

        for (final DBObject result: outputPath.results()){
            System.out.println(result);
            BasicDBObject dailyStatus = (BasicDBObject) result.get("_id");
            EshopStatusDaily eshopStatusDaily = new EshopStatusDaily();
            eshopStatusDaily.setCount((Integer)result.get("count"));
            if (dailyStatus != null) {
                eshopStatusDaily.setTime(dateBegin);
                eshopStatusDaily.setContentPath((String)dailyStatus.get("contentPath"));
                eshopStatusDaily.setHttpHost((String)dailyStatus.get("httpHost"));
                eshopStatusDaily.setLandscape((String)dailyStatus.get("landscape"));
                eshopStatusDaily.setStatus((Integer)dailyStatus.get("status"));
                mongoDBClient.getMongoOperation().save(eshopStatusDaily);
            }
        }
    }

    public StatusResult statusChart(String landscape, int statusCode) {
        BasicDBObject match;

        BasicDBObject matchParams = new BasicDBObject("status", statusCode);

        if (landscape != null){
            matchParams = matchParams.append("landscape", landscape);
        }

        match = new BasicDBObject("$match", matchParams);

        BasicDBObject group = new BasicDBObject("$group",
                new BasicDBObject("_id",
                        new BasicDBObject("day",
                                new BasicDBObject("$dayOfYear","$time"))).
                                append("count", new BasicDBObject("$sum", 1)));

        BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id.day", 1));

        List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();
        pipeline.add(match);
        pipeline.add(group);
        pipeline.add(sort);
        System.out.println(pipeline);
        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("EshopStatusDaily").aggregate(pipeline);

        StatusResult statusResult = new StatusResult();
        statusResult.setAccessStatus(new ArrayList<AccessStatus>());
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

            AccessStatus accessStatus = new AccessStatus();
            accessStatus.setCount((Integer)result.get("count"));
            accessStatus.setDate(date.toString());
            accessStatus.setStatusCode(statusCode);
            statusResult.getAccessStatus().add(accessStatus);
        }

        return statusResult;
    }

    public StatusResult status(String domain, String contentPath, Long start, Long end, int statusCode) {
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

        BasicDBObject matchParams = new BasicDBObject("status", statusCode);
        if (domain != null) {
            matchParams = matchParams.append("httpHost" , domain);
        }

        if (contentPath != null){
            matchParams = matchParams.append("contentPath", contentPath);
        }

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
        System.out.println(match.toString());

        BasicDBObject group = new BasicDBObject("$group",
                    new BasicDBObject("_id",
                            new BasicDBObject("day",
                                    new BasicDBObject("$dayOfYear","$time")))
                            .
                                    append("count",
                                            new BasicDBObject("$sum", 1)));

        BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id.day", 1));

        List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();
        pipeline.add(match);
        pipeline.add(group);
        pipeline.add(sort);
        System.out.println(pipeline);
        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("EshopStatusDaily").aggregate(pipeline);

        StatusResult statusResult = new StatusResult();
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

            AccessStatus accessStatus = new AccessStatus();
            accessStatus.setCount((Integer)result.get("count"));
            accessStatus.setDate(date.toString());
            accessStatus.setStatusCode(statusCode);
            statusResult.getAccessStatus().add(accessStatus);
        }

        return statusResult;
    }

    public StatusResult statusTop(int dayBefore, int status) {
        dayBefore = -dayBefore;
        Date startDate = Util.getBeginDateForDay(dayBefore);

        BasicDBObject timeParams = new BasicDBObject("$gte", startDate);

        BasicDBObject matchParams = new BasicDBObject("time", timeParams).append("status", status);

        BasicDBObject match = new BasicDBObject("$match", matchParams);

        BasicDBObject group = new BasicDBObject("$group",
                new BasicDBObject("_id",
                        new BasicDBObject("httpHost", "$httpHost").
                                append("contentPath", "$contentPath")).
                                append("countSum", new BasicDBObject("$sum", "$count")));

        BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("countSum", -1));

        List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();
        pipeline.add(match);
        pipeline.add(group);
        pipeline.add(sort);
        pipeline.add(new BasicDBObject("$limit", 50));
        System.out.println(pipeline);
        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("EshopStatusDaily").aggregate(pipeline);

        StatusResult statusResult = new StatusResult();
        for (final DBObject result: outputs.results()){
            System.out.println(result);
            BasicDBObject resultBasic = ((BasicDBObject)result.get("_id"));

            AccessStatus accessStatus = new AccessStatus();
            accessStatus.setCount((Integer)result.get("countSum"));
            accessStatus.setStatusCode(status);
            accessStatus.setRequesturi((String)resultBasic.get("httpHost") + (String)resultBasic.get("contentPath"));
            statusResult.getAccessStatus().add(accessStatus);
        }

        return statusResult;
    }
}
