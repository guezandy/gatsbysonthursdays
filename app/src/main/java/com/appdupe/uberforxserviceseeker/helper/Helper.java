package com.appdupe.uberforxserviceseeker.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

import com.appdupe.uberforxserviceseeker.R;

public class Helper {

	public static final String KEY_DEVICE_TOKEN = "device_token";
	public static final String KEY_REQUEST_STATUS = "request_status";
	public static final String KEY_REQUEST_ID = "request_id";
	public static final String KEY_ETA = "eta";

	public static final String EXTRA_CLIENT_REQUEST = "client_request";
	public static final String KEY_DRIVER_REACHED = "dirver_reached";
	public static final String EXTRA_ETA_RECEIVED = "eta_received";
	public static final String EXTRA_ID = "id";

	public static final String ACTION_ETA_RECEIVED = "ETAReceived";
	public static final String ACTION_GCM_TOKEN_RECEIVED = "DeviceToken";

	public static final int DEFAULT_ZOOM = 12;

	public static final String PREFERENCES_UBERCLIENT = "com.appdupe.uberclient";

	public static final String OPERATOR_PHONE = "+919428348059";

	public static void popToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static ProgressDialog showProgressDialog(Context context,
			String title, String message) {
		return ProgressDialog.show(context, title, message, false, false);
	}

	public static boolean isEmailValid(String email) {
		if (email.length() == 0) {
			return false;
		}

		Pattern pattern;
		Matcher matcher;

		final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);

		return matcher.matches();
	}

	public static boolean isInternetConnected(Context act) {
		ConnectivityManager connectivity = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}

		return false;
	}

	public static void showNetworkDialog(final Activity context) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setTitle(context.getString(R.string.no_connectivity));
		alertBuilder.setMessage(context.getString(R.string.internet_enabled));
		alertBuilder.setPositiveButton(context.getString(R.string.enable_wifi),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						context.startActivity(new Intent(
								Settings.ACTION_WIFI_SETTINGS));
					}
				});
		alertBuilder.setNeutralButton(context.getString(R.string.enable_3G),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_DATA_ROAMING_SETTINGS);
						ComponentName cName = new ComponentName(
								"com.android.phone",
								"com.android.phone.Settings");
						intent.setComponent(cName);
						context.startActivity(intent);
					}
				});
		alertBuilder.setNegativeButton(context.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						context.finish();
					}
				});
		alertBuilder.setCancelable(false);
		alertBuilder.create().show();
	}

	public static boolean isGPSEnabled(Context context) {
		final LocationManager manager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return true;
		}

		return false;
	}

	public static void showLocationDialog(final Activity context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setTitle("");
		alertDialogBuilder
				.setMessage(context.getString(R.string.gps_enabled))
				.setCancelable(false)
				.setPositiveButton(context.getString(R.string.enable),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								context.startActivity(callGPSSettingIntent);
							}
						});
		alertDialogBuilder.setNegativeButton(
				context.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						context.finish();
					}
				});
		alertDialogBuilder.create().show();
	}
}
