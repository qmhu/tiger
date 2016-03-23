package com.my.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.my.service.SearchService;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

/**
 * Handles requests for the Trigger job.
 */
@Controller
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    @RequestMapping(value = "/job/search", method = RequestMethod.GET)
    public @ResponseBody String search(@RequestParam("dayBefore") int dayBefore) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        searchService.deleteMongoDBRecord(dayBefore);
        searchService.searchViews(dayBefore);

        return "Success";
    }

    @RequestMapping(value = "/job/importall", method = RequestMethod.GET)
    public @ResponseBody String searchAll() throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        int dayBefore = 1;
        for(int i=1;i<=40; i++) {
            searchService.deleteMongoDBRecord(dayBefore);
            searchService.searchViews(dayBefore);

            dayBefore = -dayBefore;

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, dayBefore);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date dateBegin = calendar.getTime();
            System.out.println("finish import for day :" + dateBegin);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "Success";
    }

    @RequestMapping(value = "/job/meta", method = RequestMethod.GET)
    public @ResponseBody String meta() throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        searchService.generateMeta();

        return "Success";
    }

}
