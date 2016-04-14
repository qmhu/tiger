package com.my.service;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.my.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by I311862 on 2016/3/21.
 */
@Service("responseTimeService")
public class ResponseTimeService {

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

        matchParams = matchParams.append("time", timeParams);

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


}
