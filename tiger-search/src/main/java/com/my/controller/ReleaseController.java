package com.my.controller;

import io.swagger.annotations.Api;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.my.service.ElasticSearchService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles requests for the Trigger job.
 */
@Api(value = "/job")
@Controller
public class ReleaseController {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseController.class);

    @Autowired
    private ElasticSearchService elasticSearchService;

    @RequestMapping(value = "release", method = RequestMethod.GET)
    public void helloSearch() throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        elasticSearchService.searchViews();
    }

}
