package com.my.controller;

import com.my.service.EshopMetaService;
import com.my.service.StatusService;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Handles requests for the Trigger job.
 */
@Controller
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    @Autowired
    private EshopMetaService eshopMetaService;

    @Autowired
    private StatusService statusService;

    @RequestMapping(value = "/job/search", method = RequestMethod.GET)
    public @ResponseBody String search(@RequestParam("dayBefore") int dayBefore,
                                       @RequestParam("landscape") String landscape) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        searchService.deleteMongoDBRecord(dayBefore, landscape);
        searchService.searchViews(dayBefore, landscape);

        return "Success";
    }

    @RequestMapping(value = "/job/importall", method = RequestMethod.GET)
    public @ResponseBody String searchAll() throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        List<String> landscapes = new ArrayList<String>();
        landscapes.add("cn");
        landscapes.add("us");

        for(int i=1;i<=8; i++) {
            for(String landscape : landscapes){
                int dayBefore = i;
                searchService.deleteMongoDBRecord(dayBefore, landscape);
                searchService.searchViews(dayBefore, landscape);

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
        }
        return "Success";
    }

    @RequestMapping(value = "/job/meta", method = RequestMethod.GET)
    public @ResponseBody String meta() throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        eshopMetaService.generateMeta();

        return "Success";
    }

    @RequestMapping(value = "/job/status", method = RequestMethod.GET)
    public @ResponseBody String dailyStatus(@RequestParam("dayBefore") int dayBefore,
                                            @RequestParam("landscape") String landscape) {
        statusService.deleteDailyStatus(dayBefore, landscape);
        statusService.generateDailyStatus(dayBefore, landscape);
        return "Success";
    }

    @RequestMapping(value = "/job/statusall", method = RequestMethod.GET)
    public @ResponseBody String generateStatusAll() throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        List<String> landscapes = new ArrayList<String>();
        landscapes.add("cn");
        landscapes.add("us");

        for(int i=1;i<=50; i++) {
            for(String landscape : landscapes){
                int dayBefore = i;
                statusService.deleteDailyStatus(dayBefore, landscape);
                statusService.generateDailyStatus(dayBefore, landscape);

                dayBefore = -dayBefore;

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, dayBefore);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date dateBegin = calendar.getTime();
                System.out.println("finish generate status for day :" + dateBegin);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return "Success";
    }


}
