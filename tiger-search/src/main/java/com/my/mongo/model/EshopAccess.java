package com.my.mongo.model;

import com.my.elasticsearch.model.Access;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.awt.SystemColor.text;

/**
 * Created by I311862 on 2016/3/20.
 */
@Document(collection = "EshopAccess")
public class EshopAccess {

    @Id
    private String id;

    private String message;

    private String version;

    private String removeAddr;

    private String httpHost;

    private int bodySize;

    private String userAgent;

    private String schema;

    private String requestMethod;

    private String serverProtocol;

    private String contentPath;

    private String status;

    private double responseTime;

    private Date time;

    private String landscape;

    private String device;

    private String requestUri;

    private String requestType;

    private Date createTime;

    public EshopAccess(){

    }

    public EshopAccess(Access access){
        message = access.getMessage();
        version = access.getVersion();
        removeAddr = access.getRemoveAddr();
        httpHost = access.getHttpHost() == null ? "" : access.getHttpHost();
        bodySize = access.getBodySize();
        userAgent = access.getUserAgent();
        schema = access.getSchema();
        requestMethod = access.getRequestMethod();
        contentPath = access.getRequestUri();
        requestUri = httpHost + contentPath;
        status = access.getStatus();
        try {
            responseTime = Double.valueOf(access.getUpstreamResponseTime()) * 1000;
        } catch (Exception ex){
        }
        try {
            /*org.joda.time.format.DateTimeFormatter parser = ISODateTimeFormat.dateTime();
            DateTime dt = parser.parseDateTime(access.getTime());
            time = dt.getMillis();*/
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
            time = format.parse(access.getTime());
        } catch (ParseException e){
            e.printStackTrace();
        }

        if (requestUri.endsWith(".css") || requestUri.endsWith(".less") || requestUri.contains(".css?") || requestUri.contains(".less?")){
            requestType = "css";
        }else if (requestUri.endsWith(".js") || requestUri.contains(".js?")){
            requestType = "js";
        } else if (requestUri.endsWith(".png") || requestUri.endsWith(".gif") || requestUri.endsWith(".jpg") || requestUri.endsWith(".jpeg")){
            requestType = "img";
        } else if (requestUri.endsWith(".woff") || requestUri.contains(".woff?")){
            requestType = "font";
        } else if (requestUri.contains(".loc?")){
            requestType = "loc";
        }else if (requestUri.contains("options.json?")){
            requestType = "setting";
        } else{
            requestType = "doc";
        }

        landscape = access.getLandscape();
        device = access.getDevice();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRemoveAddr() {
        return removeAddr;
    }

    public void setRemoveAddr(String removeAddr) {
        this.removeAddr = removeAddr;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public void setHttpHost(String httpHost) {
        this.httpHost = httpHost;
    }

    public int getBodySize() {
        return bodySize;
    }

    public void setBodySize(int bodySize) {
        this.bodySize = bodySize;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getServerProtocol() {
        return serverProtocol;
    }

    public void setServerProtocol(String serverProtocol) {
        this.serverProtocol = serverProtocol;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        status = status;
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getLandscape() {
        return landscape;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
