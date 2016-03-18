package com.my.model;

import com.google.gson.annotations.SerializedName;

import io.searchbox.annotations.JestId;

public class Access {
	
	@JestId
	private String documentId;
	
	@SerializedName("message")
	private String message;
	
	@SerializedName("@version")
	private String version;
	

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

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	

}
