package com.my.elasticsearch.model;

import com.google.gson.annotations.SerializedName;

import io.searchbox.annotations.JestId;

import java.util.Date;

public class Access {
	
	@JestId
	private String documentId;
	
	@SerializedName("message")
	private String message;

    @SerializedName("@version")
	private String version;

	@SerializedName("@remote_addr")
	private String removeAddr;

    @SerializedName("http_host")
    private String httpHost;

    @SerializedName("BodySize")
    private int bodySize;

	@SerializedName("http_user_agent")
	private String userAgent;

	@SerializedName("scheme")
	private String schema;

	@SerializedName("request_method")
	private String requestMethod;

	@SerializedName("server_protocol")
	private String serverProtocol;

	@SerializedName("request_uri")
	private String requestUri;

	@SerializedName("Status")
	private String Status;

	@SerializedName("upstream_response_time")
	private String upstreamResponseTime;

	@SerializedName("@timestamp")
	private String time;

	@SerializedName("landscape")
	private String landscape;

	@SerializedName("device")
	private String device;

	@SerializedName("duration_number")
	private int durationNumber;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUpstreamResponseTime() {
        return upstreamResponseTime;
    }

    public void setUpstreamResponseTime(String upstreamResponseTime) {
        this.upstreamResponseTime = upstreamResponseTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
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

    public int getDurationNumber() {
        return durationNumber;
    }

    public void setDurationNumber(int durationNumber) {
        this.durationNumber = durationNumber;
    }

    @Override
    public String toString() {
        return "Access{" +
                "documentId='" + documentId + '\'' +
                ", message='" + message + '\'' +
                ", version='" + version + '\'' +
                ", removeAddr='" + removeAddr + '\'' +
                ", httpHost='" + httpHost + '\'' +
                ", bodySize=" + bodySize +
                ", userAgent='" + userAgent + '\'' +
                ", schema='" + schema + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", serverProtocol='" + serverProtocol + '\'' +
                ", requestUri='" + requestUri + '\'' +
                ", Status='" + Status + '\'' +
                ", upstreamResponseTime=" + upstreamResponseTime +
                ", time='" + time + '\'' +
                ", landscape='" + landscape + '\'' +
                ", device='" + device + '\'' +
                ", durationNumber=" + durationNumber +
                '}';
    }
}
