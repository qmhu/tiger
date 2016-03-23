package com.my.service;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.my.mongo.config.SpringMongoConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

/**
 * Created by I311862 on 2016/3/20.
 */

@Service("mongoDBClient")
public class MongoDBClient {

    private MongoOperations mongoOperation;

    public MongoDBClient(){
        ApplicationContext ctx =
                new AnnotationConfigApplicationContext(SpringMongoConfig.class);
        mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
    }

    public MongoOperations getMongoOperation() {
        return mongoOperation;
    }

}
