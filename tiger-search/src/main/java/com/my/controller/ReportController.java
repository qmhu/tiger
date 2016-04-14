package com.my.controller;

import com.my.model.*;
import com.my.service.EshopMetaService;
import com.my.service.PageViewService;
import com.my.service.ResponseTimeService;
import com.my.service.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private StatusService statusService;

    @RequestMapping(value = "/report/domainview", method = RequestMethod.GET)
    public @ResponseBody
    DomainViewResult domainView(@RequestParam("dayBefore") int dayBefore) {
        return pageViewService.getDomainView(dayBefore);
    }

    @RequestMapping(value = "/report/pageview", method = RequestMethod.GET)
    public @ResponseBody
    PageViewResult pageView(@RequestParam("dayBefore") int dayBefore) {
        return pageViewService.getPageView(dayBefore);
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
        return statusService.status(domain, contentPath, startDate, endDate, status);
    }

    @RequestMapping(value = "/report/statusTop", method = RequestMethod.GET)
    public @ResponseBody
    StatusResult statusTop(@RequestParam("dayBefore") int dayBefore,
                                @RequestParam(value = "status") int status) {
        return statusService.statusTop(dayBefore, status);
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
            StatusResult statusResult = statusService.statusChart(landscape, statusCode);
            statusResults.add(statusResult);
        }

        return statusResults;
    }


}
