package com.my.service;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.my.model.AccessStatus;
import com.my.model.OCCAccessStatus;
import com.my.model.OCCStatusResult;
import com.my.model.StatusResult;
import com.my.mongo.model.EshopStatusDaily;
import com.my.mongo.model.OCCStatusDaily;
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
@Service("occStatusService")
public class OCCStatusService {

    @Autowired
    private MongoDBClient mongoDBClient;

    public void deleteDailyStatus(int dayBefore){
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
                getCollection("OCCStatusDaily").remove(
                new BasicDBObject("time",new BasicDBObject("$gte", dateBegin).append("$lt", dateEnd)));
        System.out.println(result);
    }

    public void generateDailyStatus(int dayBefore) {
        dayBefore = -dayBefore;

        Date dateBegin = Util.getBeginDateForDay(dayBefore);
        System.out.println(dateBegin);

        Date dateEnd = Util.getEndDateForDay(dayBefore);
        System.out.println(dateEnd);

        BasicDBObject timeParams = new BasicDBObject("$gte", dateBegin).append("$lt", dateEnd);

        AggregationOutput outputPath = mongoDBClient.getMongoOperation().getCollection("OCCAccess").aggregate(asList(
                new BasicDBObject("$match",
                        new BasicDBObject("requestType","doc").
                                append("status", new BasicDBObject("$gte", 400)).
                                append("time", timeParams)),
                new BasicDBObject("$group",
                        new BasicDBObject("_id", new BasicDBObject("contentPath", "$contentPath").
                                append("landscape", "$landscape").
                                append("status", "$status")).
                                append("count", new BasicDBObject("$sum", 1)))));

        for (final DBObject result: outputPath.results()){
            System.out.println(result);
            BasicDBObject dailyStatus = (BasicDBObject) result.get("_id");
            OCCStatusDaily occStatusDaily = new OCCStatusDaily();
            occStatusDaily.setCount((Integer)result.get("count"));
            if (dailyStatus != null) {
                occStatusDaily.setTime(dateBegin);
                occStatusDaily.setContentPath((String)dailyStatus.get("contentPath"));
                //occStatusDaily.setHttpHost((String)dailyStatus.get("httpHost"));
                occStatusDaily.setLandscape((String)dailyStatus.get("landscape"));
                occStatusDaily.setStatus((Integer)dailyStatus.get("status"));
                mongoDBClient.getMongoOperation().save(occStatusDaily);
            }
        }
    }

    public OCCStatusResult statusChart(String landscape, int statusCode) {
        int dayBefore = -30;
        Date dateBegin = Util.getBeginDateForDay(dayBefore);
        System.out.println(dateBegin);

        BasicDBObject timeParams = new BasicDBObject("$gte", dateBegin);

        BasicDBObject match;

        BasicDBObject matchParams = new BasicDBObject("status", statusCode);

        if (landscape != null){
            matchParams = matchParams.append("landscape", landscape);
        }

        matchParams.append("time", timeParams);

        match = new BasicDBObject("$match", matchParams);

        BasicDBObject group = new BasicDBObject("$group",
                new BasicDBObject("_id",
                        new BasicDBObject("day",
                                new BasicDBObject("$dayOfYear","$time"))).
                                append("count", new BasicDBObject("$sum", "$count")));

        BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id.day", 1));

        List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();
        pipeline.add(match);
        pipeline.add(group);
        pipeline.add(sort);
        System.out.println(pipeline);
        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("OCCStatusDaily").aggregate(pipeline);

        OCCStatusResult occStatusResult = new OCCStatusResult();
        occStatusResult.setAccessStatus(new ArrayList<OCCAccessStatus>());

        for(int i=30;i >= 1; i--){
            OCCAccessStatus occAccessStatus = new OCCAccessStatus();
            Date dateDay = Util.getBeginDateForDay(-i);
            occAccessStatus.setDate(dateDay.toString());
            occAccessStatus.setStatusCode(statusCode);
            occStatusResult.getAccessStatus().add(occAccessStatus);
        }

        for (final DBObject result: outputs.results()){
            System.out.println(result);

            String day = ((BasicDBObject)result.get("_id")).get("day").toString();

            for (OCCAccessStatus occAccessStatus : occStatusResult.getAccessStatus()){
                Date predefineDate = new Date(occAccessStatus.getDate());
                Calendar calendarPredefine = Calendar.getInstance();
                calendarPredefine.setTime(predefineDate);

                if (Integer.valueOf(day) == calendarPredefine.get(Calendar.DAY_OF_YEAR)){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, 0);
                    calendar.set(Calendar.DAY_OF_MONTH, 0);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.add(Calendar.DAY_OF_YEAR, Integer.valueOf(day));
                    Date date = calendar.getTime();

                    occAccessStatus.setDate(date.toString());
                    occAccessStatus.setCount((Integer)result.get("count"));
                    occAccessStatus.setStatusCode(statusCode);
                    break;
                }
            }
        }

        return occStatusResult;
    }

    public OCCStatusResult status(String contentPath, Long start, Long end, int statusCode) {
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
                                            new BasicDBObject("$sum", "$count")));

        BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id.day", 1));

        List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();
        pipeline.add(match);
        pipeline.add(group);
        pipeline.add(sort);
        System.out.println(pipeline);
        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("OCCStatusDaily").aggregate(pipeline);

        OCCStatusResult occStatusResult = new OCCStatusResult();
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

            OCCAccessStatus occAccessStatus = new OCCAccessStatus();
            occAccessStatus.setCount((Integer)result.get("count"));
            occAccessStatus.setDate(date.toString());
            occAccessStatus.setStatusCode(statusCode);
            occStatusResult.getAccessStatus().add(occAccessStatus);
        }

        return occStatusResult;
    }

    public OCCStatusResult statusTop(int dayBefore, int status) {
        dayBefore = -dayBefore;
        Date startDate = Util.getBeginDateForDay(dayBefore);

        BasicDBObject timeParams = new BasicDBObject("$gte", startDate);

        BasicDBObject matchParams = new BasicDBObject("time", timeParams).append("status", status);

        BasicDBObject match = new BasicDBObject("$match", matchParams);

        BasicDBObject group = new BasicDBObject("$group",
                new BasicDBObject("_id",
                        new BasicDBObject("contentPath", "$contentPath")).
                        append("countSum", new BasicDBObject("$sum", "$count")));

        BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("countSum", -1));

        List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();
        pipeline.add(match);
        pipeline.add(group);
        pipeline.add(sort);
        pipeline.add(new BasicDBObject("$limit", 50));
        System.out.println(pipeline);
        AggregationOutput outputs = mongoDBClient.getMongoOperation().getCollection("OCCStatusDaily").aggregate(pipeline);

        OCCStatusResult occStatusResult = new OCCStatusResult();
        for (final DBObject result: outputs.results()){
            System.out.println(result);
            BasicDBObject resultBasic = ((BasicDBObject)result.get("_id"));

            OCCAccessStatus occAccessStatus = new OCCAccessStatus();
            occAccessStatus.setCount((Integer)result.get("countSum"));
            occAccessStatus.setStatusCode(status);
            occAccessStatus.setContentPath((String)resultBasic.get("contentPath"));
            occStatusResult.getAccessStatus().add(occAccessStatus);
        }

        return occStatusResult;
    }
}
