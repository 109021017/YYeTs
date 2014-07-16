package com.willme.yyets.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Intent serviceIntent = new Intent(context, NotificationService.class);
		context.startService(serviceIntent);
		
	}

}
