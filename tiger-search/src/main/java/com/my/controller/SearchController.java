package com.my.controller;

import com.my.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    private EshopAccessService eshopAccessService;

    @Autowired
    private OCCAccessService occAccessService;

    @Autowired
    private EshopMetaService eshopMetaService;

    @Autowired
    private EshopStatusService eshopStatusService;

    @Autowired
    private OCCStatusService occStatusService;

    @RequestMapping(value = "/job/importeshop", method = RequestMethod.GET)
    public @ResponseBody String importeshop(@RequestParam("dayBefore") int dayBefore,
                                       @RequestParam("landscape") String landscape) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        eshopAccessService.deleteMongoDBRecord(dayBefore, landscape);
        eshopAccessService.searchViews(dayBefore, landscape);

        return "Success";
    }

    @RequestMapping(value = "/job/importalleshop", method = RequestMethod.GET)
    public @ResponseBody String importalleshop() throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        List<String> landscapes = new ArrayList<String>();
        landscapes.add("cn");
        landscapes.add("us");

        for(int i=1;i<=8; i++) {
            for(String landscape : landscapes){
                int dayBefore = i;
                eshopAccessService.deleteMongoDBRecord(dayBefore, landscape);
                eshopAccessService.searchViews(dayBefore, landscape);

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

    @RequestMapping(value = "/job/importocc", method = RequestMethod.GET)
    public @ResponseBody String importocc(@RequestParam("dayBefore") int dayBefore) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        occAccessService.deleteMongoDBRecord(dayBefore);
        occAccessService.pullOCCAccess(dayBefore);

        return "Success";
    }

    @RequestMapping(value = "/job/importallocc", method = RequestMethod.GET)
    public @ResponseBody String importallocc() throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        List<String> landscapes = new ArrayList<String>();

        for(int i=1;i<=30; i++) {
            int dayBefore = i;
            occAccessService.deleteMongoDBRecord(dayBefore);
            occAccessService.pullOCCAccess(dayBefore);

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
        eshopMetaService.generateMeta();

        return "Success";
    }

    @RequestMapping(value = "/job/status", method = RequestMethod.GET)
    public @ResponseBody String dailyStatus(@RequestParam("dayBefore") int dayBefore) {
        eshopStatusService.deleteDailyStatus(dayBefore);
        eshopStatusService.generateDailyStatus(dayBefore);
        return "Success";
    }

    @RequestMapping(value = "/job/occstatus", method = RequestMethod.GET)
    public @ResponseBody String occDailyStatus(@RequestParam("dayBefore") int dayBefore) {
        occStatusService.deleteDailyStatus(dayBefore);
        occStatusService.generateDailyStatus(dayBefore);
        return "Success";
    }

    @RequestMapping(value = "/job/statusall", method = RequestMethod.GET)
    public @ResponseBody String generateStatusAll() throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        for(int i=1;i<=50; i++) {
            int dayBefore = i;
            eshopStatusService.deleteDailyStatus(dayBefore);
            eshopStatusService.generateDailyStatus(dayBefore);

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
        return "Success";
    }

    @RequestMapping(value = "/job/occstatusall", method = RequestMethod.GET)
    public @ResponseBody String generateOCCStatusAll() throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        for(int i=1;i<=50; i++) {
            int dayBefore = i;
            occStatusService.deleteDailyStatus(dayBefore);
            occStatusService.generateDailyStatus(dayBefore);

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
        return "Success";
    }


}
