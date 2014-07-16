package com.willme.yyets.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.willme.yyets.db.contracts.TableFavorite;
import com.willme.yyets.db.contracts.TableResourceInfo;
import com.willme.yyets.entities.YYResource;

public class DBHelper extends SQLiteOpenHelper {


	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "yyests.db"; 
	
    private SQLiteDatabase mDatabase;
    
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		mDatabase = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TableFavorite.SQL_CREATE_TBL);
		db.execSQL(TableResourceInfo.SQL_CREATE_TBL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	
	public ArrayList<YYResource> getUpdatedRes(ArrayList<YYResource> newRes){
		ArrayList<YYResource> result = new ArrayList<YYResource>();
		String SQL_IS_RES_EXISTED = 
				"SELECT * " +
				" FROM " +
				TableFavorite.TABLE_NAME +
				" WHERE " + 
				TableFavorite.ID +
				" = ?";
		
		String SQL_IS_RES_UPDATED = 
				"SELECT * "+
				" FROM " +
				TableFavorite.TABLE_NAME +
				" WHERE "+
				TableFavorite.ID +
				" = ?" +
				" AND " +
				TableFavorite.UPDATE_TIME +
				" != ?"
				;
		
		Calendar twoHoursAgo = Calendar.getInstance(Locale.CHINA);
		twoHoursAgo.add(Calendar.HOUR_OF_DAY, -2);
		for(YYResource res :newRes){

			if(res.getUpdateTime().before(twoHoursAgo))
				continue;
			Cursor cursor = mDatabase.rawQuery(SQL_IS_RES_EXISTED, new String[]{res.getName()});
			if(cursor.getCount() == 0){
				result.add(res);
				cursor.close();
				continue;
			}
			cursor.close();
			
			Cursor cursor2 = mDatabase.rawQuery(SQL_IS_RES_UPDATED, new String[]{res.getName(), String.valueOf(res.getUpdateTime().getTimeInMillis())});
			if(cursor2.getCount() > 0){
				result.add(res);
			}
			cursor2.close();
		}
		return result;
	}
	
	public void updateFavoriteRes(ArrayList<YYResource> newRes){
		
		clearFavorites();
		for(YYResource r:newRes){
			addFavorite(r);
		}
		
	}
	
	public ArrayList<YYResource> getFavoriteRes(){
		ArrayList<YYResource> result = new ArrayList<YYResource>();
		Cursor c = mDatabase.rawQuery(
				" SELECT * FROM " +
				TableFavorite.TABLE_NAME +" a, "+ 
				TableResourceInfo.TABLE_NAME + " b " +
				" WHERE a."+TableFavorite.ID +
				" = b."+TableResourceInfo.ID +
				" ORDER BY a."+TableFavorite.UPDATE_TIME + 
				" DESC ", null);
		if(c.getCount() > 0){
			int updateTimeIndex = c.getColumnIndexOrThrow(TableFavorite.UPDATE_TIME);
			
			int idIndex = c.getColumnIndexOrThrow(TableResourceInfo.ID);
			int nameIndex = c.getColumnIndexOrThrow(TableResourceInfo.NAME);
			int updateInfoIndext = c.getColumnIndexOrThrow(TableResourceInfo.LAST_UPDATE_INFO);
			int imgUrlIndex = c.getColumnIndexOrThrow(TableResourceInfo.IMG_URL);
			int channelIndext = c.getColumnIndexOrThrow(TableResourceInfo.CHANNEL);
			int englishNameIndex = c.getColumnIndexOrThrow(TableResourceInfo.ENGLISH_NAME);
			int categoriesIndex = c.getColumnIndexOrThrow(TableResourceInfo.CATEGORIES);
			int firstAirIndex = c.getColumnIndexOrThrow(TableResourceInfo.FIRST_AIR_TIME);
			int summaryIndex = c.getColumnIndexOrThrow(TableResourceInfo.SUMMARY);
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				YYResource resource = new YYResource();
				resource.setId(c.getString(idIndex));
				resource.setChannel(c.getString(channelIndext));
				resource.setImgUrl(c.getString(imgUrlIndex));
				resource.setName(c.getString(nameIndex));
				resource.setUpdateInfo(c.getString(updateInfoIndext));
				resource.setUpdateTime(c.getLong(updateTimeIndex));
				result.add(resource);
			}
		}
		return result;
		
	}
	
	public void clearFavorites(){
		
		String SQL_CLEAR_FAVORITES = "DELETE FROM "+TableFavorite.TABLE_NAME;
		mDatabase.execSQL(SQL_CLEAR_FAVORITES);
		
	}
	
	public void addFavorite(YYResource res){
		ContentValues values = new ContentValues();
		values.put(TableFavorite.ID, res.getId());
		values.put(TableFavorite.UPDATE_TIME, res.getUpdateTime().getTimeInMillis());
		mDatabase.insert(TableFavorite.TABLE_NAME, null, values);
		String[] whereArgs = new String[]{res.getId()};
		String  whereClause = TableResourceInfo.ID + " = ? ";
		Cursor c = mDatabase.rawQuery(
				" SELECT * FROM " + 
				TableResourceInfo.TABLE_NAME +
				" WHERE " + 
				whereClause, whereArgs);
		values.clear();
		values.put(TableResourceInfo.NAME, res.getName());
		values.put(TableResourceInfo.ID, res.getId());
		values.put(TableResourceInfo.LAST_UPDATE_INFO, res.getUpdateInfo());
		values.put(TableResourceInfo.CHANNEL, res.getChannel());
		if(!TextUtils.isEmpty(res.getSmallImgUrl())){
			values.put(TableResourceInfo.IMG_URL, res.getSmallImgUrl());
		}
		if(c.getCount() > 0){
			mDatabase.update(TableResourceInfo.TABLE_NAME, values, whereClause, whereArgs);
		}else{
			mDatabase.insert(TableResourceInfo.TABLE_NAME, null, values);
		}
		c.close();
	}

}
