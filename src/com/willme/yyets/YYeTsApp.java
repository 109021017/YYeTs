package com.willme.yyets;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.willme.yyets.api.YYeTsRestClient;
import com.willme.yyets.push.NotificationService;

public class YYeTsApp extends Application {

    private static SharedPreferences settings;
    
	@Override
	public void onCreate() {
		super.onCreate();
		YYeTsRestClient.init(this);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		if(!TextUtils.isEmpty(CookiesInfo.getGKEY())){
			startService(new Intent(this, NotificationService.class));
		}
	}
	
	public static class UserInfo{
		
		public static String getUserId(){
			return settings.getString("userId", null);
		}
		
		public static void setUserId(String uid){
			settings.edit().putString("userId", uid).commit();
		}
		
		public static String getNickname(){
			return settings.getString("nickname", null);
		}
		
		public static void setNickname(String nickname){
			settings.edit().putString("nickname", nickname).commit();
		}

		public static String getTinyAvatarUrl(){
			return settings.getString("avatarUrl", null)+"t.jpg";
		}
		
		public static String getSmallAvatarUrl(){
			return settings.getString("avatarUrl", null)+"s.jpg";
		}
		
		public static String getBigAvatarUrl(){
			return settings.getString("avatarUrl", null)+"b.jpg";
		}
		
		public static void setTinyAvatarUrl(String avartarUrl){
			String baseUrl = avartarUrl.substring(0, avartarUrl.length()-6);
			settings.edit().putString("avatarUrl", baseUrl).commit();
		}
	}
	
	public static class CookiesInfo{
		public static String getGINFO(){
			return settings.getString("Cookies_GINFO", "");
		}
		
		public static void setGINFO(String ginfo){
			settings.edit().putString("Cookies_GINFO", ginfo).commit();
		}
		
		public static String getGKEY(){
			return settings.getString("Cookies_GKEY", "");
		}
		
		public static void setGKEY(String gkey){
			settings.edit().putString("Cookies_GKEY", gkey).commit();
		}
		
	}
	
}
