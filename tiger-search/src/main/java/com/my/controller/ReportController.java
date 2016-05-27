package com.my.controller;

import com.my.model.*;
import com.my.mongo.model.EshopAccess;
import com.my.mongo.model.OCCAccess;
import com.my.realtime.RealtimeQueryService;
import com.my.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles requests for the report.
 */
@Controller
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ResponseTimeService responseTimeService;

    @Autowired
    private EshopMetaService eshopMetaService;

    @Autowired
    private PageViewService pageViewService;

    @Autowired
    private EshopStatusService eshopStatusService;

    @Autowired
    private OCCStatusService occStatusService;

    @Autowired
    private EshopAccessService eshopAccessService;

    @Autowired
    private OCCAccessService occAccessService;

    @Autowired
    private RealtimeQueryService realtimeQueryService;

    @RequestMapping(value = "/report/realtime", method = RequestMethod.GET)
    public @ResponseBody
    ResponseTimeResult queryInRealTime(@RequestParam(value = "startDate", required=false) Long startDate,
                                 @RequestParam(value = "endDate", required=false) Long endDate,
                                 @RequestParam(value = "domain", required=false) String domain,
                                 @RequestParam(value = "landscape", required=false) String landscape,
                                 @RequestParam(value = "interval", required=false) String interval) {
        try {
            String[] domainArray;
            if (domain != null) {
                domainArray = new String[1];
                if (domain.indexOf(";") > -1) {
                    domainArray = domain.split(";");
                } else {
                    domainArray[0] = domain;
                }
            }else {
                domainArray = null;
            }
            return realtimeQueryService.realtimeQueryService(landscape, domainArray, "",startDate, endDate, interval);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/report/domainview", method = RequestMethod.GET)
    public @ResponseBody
    DomainViewResult domainView(@RequestParam("dayBefore") int dayBefore) {
        return pageViewService.getDomainView(dayBefore, 200);
    }

    @RequestMapping(value = "/report/pageview", method = RequestMethod.GET)
    public @ResponseBody
    PageViewResult pageView(@RequestParam("dayBefore") int dayBefore) {
        return pageViewService.getPageView(dayBefore, 200);
    }

    @RequestMapping(value = "/report/domainviewchart", method = RequestMethod.GET)
    public @ResponseBody
    DomainViewResult domainViewChart(@RequestParam(value = "startDate", required=false) Long startDate,
                                   @RequestParam(value = "endDate", required=false) Long endDate,
                                   @RequestParam(value = "domain", required=false) String domain,
                                   @RequestParam(value = "landscape", required=false) String landscape
                                   ) {
        return pageViewService.getPageViewChart(startDate, endDate, domain, landscape);
    }


    @RequestMapping(value = "/report/responseTime", method = RequestMethod.GET)
    public @ResponseBody
    ResponseTimeResult responseTimeByDay(@RequestParam(value = "domain", required=false) String domain,
                                         @RequestParam(value = "contentPath", required=false) String contentPath,
                                         @RequestParam(value = "startDate", required=false) Long startDate,
                                         @RequestParam(value = "endDate", required=false) Long endDate,
                                         @RequestParam(value = "aggregate", required=false) String aggregate) {
        return responseTimeService.responseTime(domain, contentPath, startDate, endDate, aggregate);
    }

    @RequestMapping(value = "/report/domains", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getDomains(@RequestParam(value = "landscape", required=false) String landscape) {
        return eshopMetaService.getDomains(landscape);
    }

    @RequestMapping(value = "/report/contentPath", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getContentPath(@RequestParam("domain") String domain,
                                @RequestParam(value = "landscape", required=false) String landscape,
                                @RequestParam(value = "limit", required=false) int limit) {
        return eshopMetaService.getContentPath(domain, landscape, limit);
    }

    @RequestMapping(value = "/report/status", method = RequestMethod.GET)
    public @ResponseBody
    StatusResult getStatus(@RequestParam(value = "domain", required=false) String domain,
                                         @RequestParam(value = "contentPath", required=false) String contentPath,
                                         @RequestParam(value = "startDate", required=false) Long startDate,
                                         @RequestParam(value = "endDate", required=false) Long endDate,
                                         @RequestParam(value = "status", required=false) int status) {
        return eshopStatusService.status(domain, contentPath, startDate, endDate, status);
    }

    @RequestMapping(value = "/report/access", method = RequestMethod.GET)
    public @ResponseBody
    List<EshopAccess> getAccesses(@RequestParam(value = "httpHost", required=false) String httpHost,
                                  @RequestParam(value = "contentPath", required=false) String contentPath,
                                  @RequestParam(value = "responseTime", required=false) Integer responseTime,
                                @RequestParam(value = "startDate", required=false) Long startDate,
                                @RequestParam(value = "endDate", required=false) Long endDate,
                                @RequestParam(value = "status", required=false) int status,
                                  @RequestParam(value = "limit", required=false) Integer limit) {
        return eshopAccessService.getAccesses(httpHost, contentPath, responseTime, startDate, endDate, status, limit);
    }

    @RequestMapping(value = "/report/occaccess", method = RequestMethod.GET)
    public @ResponseBody
    List<OCCAccess> getOCCAccesses(@RequestParam(value = "contentPath", required=false) String contentPath,
                                   @RequestParam(value = "startDate", required=false) Long startDate,
                                   @RequestParam(value = "endDate", required=false) Long endDate,
                                   @RequestParam(value = "status", required=false) int status,
                                   @RequestParam(value = "limit", required=false) Integer limit) {
        return occAccessService.getOCCAccesses(contentPath, startDate, endDate, status, limit);
    }

    @RequestMapping(value = "/report/statusTop", method = RequestMethod.GET)
    public @ResponseBody
    StatusResult statusTop(@RequestParam("dayBefore") int dayBefore,
                                @RequestParam(value = "status") int status) {
        return eshopStatusService.statusTop(dayBefore, status);
    }

    @RequestMapping(value = "/report/statusChart", method = RequestMethod.GET)
    public @ResponseBody List<StatusResult> getStatusChart(@RequestParam(value = "landscape", required=false) String landscape ) {
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(499);
        statusList.add(503);
        statusList.add(500);
        statusList.add(403);
        statusList.add(400);
        statusList.add(404);

        List<StatusResult> statusResults = new ArrayList<StatusResult>();
        for (Integer statusCode : statusList){
            StatusResult statusResult = eshopStatusService.statusChart(landscape, statusCode);
            statusResults.add(statusResult);
        }

        return statusResults;
    }

    @RequestMapping(value = "/report/occstatus", method = RequestMethod.GET)
    public @ResponseBody
    OCCStatusResult getOCCStatus(@RequestParam(value = "contentPath", required=false) String contentPath,
                           @RequestParam(value = "startDate", required=false) Long startDate,
                           @RequestParam(value = "endDate", required=false) Long endDate,
                           @RequestParam(value = "status", required=false) int status) {
        return occStatusService.status(contentPath, startDate, endDate, status);
    }

    @RequestMapping(value = "/report/occStatusTop", method = RequestMethod.GET)
    public @ResponseBody
    OCCStatusResult occStatusTop(@RequestParam("dayBefore") int dayBefore,
                           @RequestParam(value = "status") int status) {
        return occStatusService.statusTop(dayBefore, status);
    }

    @RequestMapping(value = "/report/occStatusChart", method = RequestMethod.GET)
    public @ResponseBody List<OCCStatusResult> getOCCStatusChart(@RequestParam(value = "landscape", required=false) String landscape ) {
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(499);
        statusList.add(503);
        statusList.add(500);
        statusList.add(403);
        statusList.add(400);
        statusList.add(404);

        List<OCCStatusResult> statusResults = new ArrayList<OCCStatusResult>();
        for (Integer statusCode : statusList){
            OCCStatusResult statusResult = occStatusService.statusChart(landscape, statusCode);
            statusResults.add(statusResult);
        }

        return statusResults;
    }

}
