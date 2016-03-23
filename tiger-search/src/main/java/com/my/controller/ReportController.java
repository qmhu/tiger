package com.my.controller;

import com.my.model.*;
import com.my.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Handles requests for the report.
 */
@Controller
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @RequestMapping(value = "/report/domainview", method = RequestMethod.GET)
    public @ResponseBody
    DomainViewResult domainView(@RequestParam("dayBefore") int dayBefore) {
        return reportService.getDomainView(dayBefore);
    }

    @RequestMapping(value = "/report/pageview", method = RequestMethod.GET)
    public @ResponseBody
    PageViewResult pageView(@RequestParam("dayBefore") int dayBefore) {
        return reportService.getPageView(dayBefore);
    }

    @RequestMapping(value = "/report/responseTime", method = RequestMethod.GET)
    public @ResponseBody
    ResponseTimeResult responseTimeByUrl(@RequestParam(value = "url") String url) {
        return reportService.responseTimeByUrl(url);
    }

    @RequestMapping(value = "/report/responseTimeByDay", method = RequestMethod.GET)
    public @ResponseBody
    ResponseTimeResult responseTimeByDay(@RequestParam(value = "url", required=false) String url) {
        return reportService.responseTimeByDay(url);
    }

    @RequestMapping(value = "/report/domains", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getDomains() {
        return reportService.getDomains();
    }

    @RequestMapping(value = "/report/contentPath", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getContentPath(@RequestParam("domain") String domain ) {
        return reportService.getContentPath(domain);
    }


}
