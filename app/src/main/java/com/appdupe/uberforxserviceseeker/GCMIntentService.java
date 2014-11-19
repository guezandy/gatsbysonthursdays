package com.appdupe.uberforxserviceseeker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.appdupe.uberforxserviceseeker.gcm.CommonUtilities;
import com.appdupe.uberforxserviceseeker.gcm.ServerUtilities;
import com.appdupe.uberforxserviceseeker.helper.Helper;
import com.appdupe.uberforxserviceseeker.helper.JSONHelper;
import com.appdupe.uberforxserviceseeker.helper.PreferenceHelper;
import com.google.android.gcm.GCMBaseIntentService;
import com.appdupe.uberforxserviceseeker.R;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(CommonUtilities.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i("GCM", "Device registered: regId = " + registrationId);

		new PreferenceHelper(context).putDeviceToken(registrationId);
		LocalBroadcastManager.getInstance(context).sendBroadcast(
				new Intent(Helper.ACTION_GCM_TOKEN_RECEIVED));

		ServerUtilities.register(context, "name", "email", registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		ServerUtilities.unregister(context, registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		String jsonString = intent.getExtras().getString("message");

		Log.i("GCM", "Received message.. " + jsonString);

		int id = JSONHelper.getID(jsonString);

		Intent pushIntent = new Intent(Helper.ACTION_ETA_RECEIVED);
		pushIntent.putExtra(Helper.EXTRA_ETA_RECEIVED, jsonString);

		if (id == 1) {
			new PreferenceHelper(context).putETATimeInMillis(System
					.currentTimeMillis()
					+ JSONHelper.getETAReceived(jsonString));
			new PreferenceHelper(this).putDriverReached(false);
			generateNotification(id,
					context.getString(R.string.x_Service_Provider_responded));
		} else if (id == 2) {
			new PreferenceHelper(this).putDriverReached(false);
			new PreferenceHelper(context).putETATimeInMillis(-1);
			new PreferenceHelper(context).putClientRequestActive(false);
			generateNotification(id, context.getString(R.string.job_finished));
		} else if (id == 4) {
			new PreferenceHelper(this).reset();
		} else if (id == 5) {
			new PreferenceHelper(this).putDriverReached(true);
			System.out.println("====>Driver Reached!!");
			generateNotification(id,
					context.getString(R.string.x_Service_Provider_reached_desc));
		}

		LocalBroadcastManager.getInstance(context).sendBroadcast(pushIntent);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i("GCM", "Delete");
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i("GCM", "Error : " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		//Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	private void generateNotification(int id, String text) {
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra(Helper.EXTRA_ID, id);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getResources().getString(R.string.app_name))
				.setStyle(new NotificationCompat.BigTextStyle().bigText(text))
				.setContentText(text)
				.setDefaults(
						Notification.DEFAULT_SOUND
								| Notification.DEFAULT_VIBRATE);

		mBuilder.setContentIntent(contentIntent);
		mBuilder.setAutoCancel(true);

		Notification notification = mBuilder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(0, mBuilder.build());

	}
}
