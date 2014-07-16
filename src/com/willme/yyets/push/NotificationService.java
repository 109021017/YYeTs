package com.willme.yyets.push;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.willme.yyets.R;
import com.willme.yyets.YYeTsApp;
import com.willme.yyets.api.FavoriteRequest;
import com.willme.yyets.db.DBHelper;
import com.willme.yyets.entities.YYResource;

public class NotificationService extends IntentService{

	public NotificationService() {
		super("NotificationService");
	}

	public NotificationService(String name) {
		super("NotificationService");
	}
	
	public static final String EXTRA_PERIOD = "period";
	private AlarmManager mAlarmManager;
	private DBHelper mDBHelper;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		mDBHelper = new DBHelper(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mDBHelper.close();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(TextUtils.isEmpty(YYeTsApp.UserInfo.getUserId())){
			return;
		}
		int p = intent.getIntExtra(EXTRA_PERIOD, 0);
		ArrayList<YYResource> list = FavoriteRequest.getFavoriteListMoblie();
		
		findUpdateAndNotfy(this, mDBHelper, list);
		
		Intent alarmIntent = new Intent(this, NotificationService.class);
		alarmIntent.putExtra(EXTRA_PERIOD, (p+1)%3);
		PendingIntent operation = PendingIntent.getService(this,
				1, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
		Calendar nextTime = Calendar.getInstance();
		nextTime.add(Calendar.MINUTE, 10);
		mAlarmManager.set(AlarmManager.RTC_WAKEUP, nextTime.getTimeInMillis(), operation);
	}

	public static void findUpdateAndNotfy(Context context,DBHelper dbHelper, ArrayList<YYResource> list){
		if(list != null && list.size()>0){
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			long oldUpdateTime = sp.getLong("last_update_time", 0);
			long lastUpdateTime = list.get(0).getUpdateTime().getTimeInMillis();
			if(oldUpdateTime != lastUpdateTime){
				if(lastUpdateTime != 0){
					ArrayList<YYResource> updateList = dbHelper.getUpdatedRes(list);
					notifyUpdate(context, updateList);
				}
				dbHelper.updateFavoriteRes(list);
				sp.edit().putLong("last_update_time", lastUpdateTime).commit();
			}
		}
	}
	
	private static void notifyUpdate(Context context, ArrayList<YYResource> updateList) {
		if(updateList.size() == 0){
			return;
		}
		NotificationManager nManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder ncomp = new NotificationCompat.Builder(context);
		if(updateList.size() == 1){
			YYResource updatedRes = updateList.get(0);
			ncomp.setContentTitle(updatedRes.getName())
			.setContentText(context.getString(R.string.notificaiton_text_one))
			.setTicker(context.getString(R.string.notificaiton_ticker, updatedRes.getName()))
			.setWhen(updatedRes.getUpdateTime().getTimeInMillis());
		}else{
			String title = context.getString(R.string.notificaiton_text_muti, updateList.size());
			String names = getNames(context, updateList);
			ncomp.setContentTitle(title)
			.setContentText(names)
			.setTicker(title+": "+names)
			.setWhen(updateList.get(0).getUpdateTime().getTimeInMillis());
		}
		ncomp.setDefaults(Notification.DEFAULT_ALL)
		.setSmallIcon(R.drawable.ic_notify_tv)
		.setAutoCancel(true);
		nManager.notify(1, ncomp.build());
	}
	
	private static String getNames(Context context, ArrayList<YYResource> resources){
		StringBuffer names = new StringBuffer();
		int len = resources.size();
		for(int i = 0; i<len;i++){
			YYResource updatedRes = resources.get(i);
			names.append(updatedRes.getName());
			if(i < len -2){
				names.append(',');
			}else if(i == len - 2){
				names.append(context.getString(R.string.notificaiton_names_and));
			}
		}
		return names.toString();
	}

}
