package com.my.model;

import com.google.gson.annotations.SerializedName;

public class AccessDetail {
	
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
	

}
