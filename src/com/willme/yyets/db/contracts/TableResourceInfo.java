package com.willme.yyets.db.contracts;

public interface TableResourceInfo{
	
	public static final String TABLE_NAME = "resource_info";
	
	public static final String ID = "id";
	
	public static final String NAME = "name";
	
	public static final String SUMMARY = "summary";
	
	public static final String CATEGORIES = "categories";
	
	public static final String ENGLISH_NAME = "english_name";
	
	public static final String FIRST_AIR_TIME = "first_air_time";
	
	public static final String CHANNEL = "channel";
	
	public static final String IMG_URL = "img_url";
	
	public static final String LAST_UPDATE_INFO = "last_update_info";
	

	/**
	 * 建表语句
	 */
	public static final String SQL_CREATE_TBL = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + 
					ID + " TEXT PRIMARY KEY, " + 
					NAME + " TEXT, " +
					SUMMARY + " TEXT, " +
					CATEGORIES + " TEXT, " +
					ENGLISH_NAME + " TEXT, " +
					FIRST_AIR_TIME + " LONG, " +
					CHANNEL + " TEXT, " +
					IMG_URL + " TEXT, " +
					LAST_UPDATE_INFO + " TEXT " +
			") ";
}
