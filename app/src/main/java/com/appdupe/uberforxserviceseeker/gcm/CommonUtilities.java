package com.appdupe.uberforxserviceseeker.gcm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class CommonUtilities {

	public static final String SERVER_URL = "";
	// http://www.deal4ghar.com/schoolapp/school_gcm.php
	public static final String SENDER_ID = "22622131410";

	public static final String TAG = "CommonUtilities";
	public static final String DISPLAY_MESSAGE_ACTION = "com.appdupe.uberclient.DISPLAY_MESSAGE";
	public static final String EXTRA_MESSAGE = "message";

	public static void displayMessage(Context context, String message) {
		Log.i(TAG, "Diaplay message");
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}
