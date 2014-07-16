package com.willme.yyets.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

	
	public static String getStringFromJson(JSONObject jObject, String name, String defaultValue){
		try {
			String res = jObject.getString(name);
			if(res.equals("null"))
				return "";
			else return res;
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	
	public static int getIntFromJson(JSONObject jObject, String name, int defaultValue){
		try {
			return jObject.getInt(name);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	
	public static boolean getBooleanFromJson(JSONObject jObject, String name, boolean defaultValue){
		try {
			return jObject.getInt(name) == 1;
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	
}
