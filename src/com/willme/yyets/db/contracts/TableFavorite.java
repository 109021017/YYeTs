package com.willme.yyets.db.contracts;

public interface TableFavorite {
	
	public static final String TABLE_NAME = "favorite";
	
	public static final String ID = "id";
	
	public static final String UPDATE_TIME = "update_time";
	
	/**
	 * 建表语句
	 */
	public static final String SQL_CREATE_TBL = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + 
					ID + " TEXT PRIMARY KEY, " + 
					UPDATE_TIME + " LONG " +
			") ";
	
}
