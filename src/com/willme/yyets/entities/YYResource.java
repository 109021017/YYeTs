package com.willme.yyets.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.text.format.DateFormat;

import com.willme.yyets.R;

public class YYResource {

	private String id;
	private String name;
	private String imgUrl;
	private String updateInfo;
	private Calendar updateTime;
	private String channel;

	public static SimpleDateFormat sUpdateTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm", Locale.CHINA);

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSmallImgUrl() {
		return imgUrl == null?null:imgUrl.replace("m_", "s_");
	}

	public String getMiddleImgUrl() {
		return imgUrl;
	}

	public String getBigImgUrl() {
		return imgUrl == null?null:imgUrl.replace("m_", "b_");
	}

	public String getHugeImgUrl() {
		return imgUrl == null?null:imgUrl.replace("m_", "");
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getUpdateInfo() {
		return updateInfo;
	}

	public void setUpdateInfo(String description) {
		this.updateInfo = description;
	}

	public Calendar getUpdateTime() {
		return updateTime;
	}
	
	public String getUpdateTimeFormated(Context context){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		if(updateTime.after(calendar)){
			long dtTime = updateTime.getTimeInMillis() - calendar.getTimeInMillis();
			return context.getString(R.string.update_time_mins_ago, dtTime%(1000*60));
		}
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if(updateTime.after(calendar)){
			return DateFormat.format(context.getString(R.string.update_time_today), updateTime).toString();
		}
		calendar.add(Calendar.DATE, -1);
		if(updateTime.after(calendar)){
			return DateFormat.format(context.getString(R.string.update_time_yesterday), updateTime).toString();
		}
		return DateFormat.format(context.getString(R.string.date_time_format), updateTime).toString();
	}

	public void setUpdateTime(long milliseconds) {
		updateTime = Calendar.getInstance();
		updateTime.setTimeInMillis(milliseconds);
	}

	public void setUpdateTime(String formatedTime) {
		try {
			updateTime = Calendar.getInstance();
			updateTime.setTime(sUpdateTimeFormat.parse(formatedTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "id = " + id + ", name = " + name + ", desc = " + updateInfo
				+ ", updatetime = "
				+ sUpdateTimeFormat.format(updateTime.getTime())
				+ ", channel = " + channel
				+ "\n imgUrl = "+imgUrl;
	}
}
