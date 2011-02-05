package com.openrobot.common;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONMessage extends JSONObject {
	
	public static final String INSTRUCTION_KEY = "INSTRUCTION";
	public static final String SUBSCRIPTION_KEY = "SUBSCRIPTION_KEY";
	
	public static final int SUBSCRIPTION_VALUE_CONTROL = 0x1;

	public JSONMessage(String jsonString) throws JSONException {
		super(jsonString);
	}
	
	public Integer getIntForKey(String key) {
		if (this.has(key)) {
			try {
				return this.getInt(key);
			} catch (JSONException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public String getStringForKey(String key) {
		if (this.has(key)) {
			try {
				return this.getString(key);
			} catch (JSONException e) {
				return null;
			}
		} else {
			return null;
		}
	}
}
