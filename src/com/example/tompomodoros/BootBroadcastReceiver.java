package com.example.tompomodoros;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			Intent sayHelloIntent = new Intent(context, Tomato.class);
			sayHelloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				context.startActivity(sayHelloIntent);
			} catch (Exception e) {
				System.out.println("error = " + e.getMessage());
			}
		}
	}
}
