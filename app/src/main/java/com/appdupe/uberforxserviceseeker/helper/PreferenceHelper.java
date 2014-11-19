package com.appdupe.uberforxserviceseeker.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceHelper {

	private SharedPreferences app_prefs;

	public PreferenceHelper(Context context) {
		app_prefs = context.getSharedPreferences(Helper.PREFERENCES_UBERCLIENT,
				Context.MODE_PRIVATE);
	}

	public void putDeviceToken(String value) {
		Editor edit = app_prefs.edit();
		edit.putString(Helper.KEY_DEVICE_TOKEN, value);
		edit.commit();
	}

	public String getDeviceToken() {
		return app_prefs.getString(Helper.KEY_DEVICE_TOKEN, "");
	}

	public boolean isDeviceTokenAquired() {
		if (getDeviceToken().equals("")) {
			return false;
		} else {
			return true;
		}
	}

	public void putClientRequestActive(boolean status) {
		Editor edit = app_prefs.edit();
		edit.putBoolean(Helper.KEY_REQUEST_STATUS, status);
		edit.commit();
	}

	public boolean getRequestStatus() {
		return app_prefs.getBoolean(Helper.KEY_REQUEST_STATUS, false);
	}

	public void putClientRequestID(String request_id) {
		Editor edit = app_prefs.edit();
		edit.putString(Helper.KEY_REQUEST_ID, request_id);
		edit.commit();
	}

	public String getRequestID() {
		return app_prefs.getString(Helper.KEY_REQUEST_ID, "");
	}

	public void putETATimeInMillis(long millis) {
		Editor edit = app_prefs.edit();
		edit.putLong(Helper.KEY_ETA, millis);
		edit.commit();
	}

	public long getETATimeInMillis() {
		return app_prefs.getLong(Helper.KEY_ETA, -1)
				- System.currentTimeMillis();
	}

	public boolean isETAOver() {
		long eta = app_prefs.getLong(Helper.KEY_ETA, -1);

		if (System.currentTimeMillis() > eta) {
			return true;
		}

		return false;
	}

	public void reset() {

		putClientRequestID("");
		putDriverReached(false);
		putClientRequestActive(false);
		putETATimeInMillis(-1);
	}

	public void putDriverReached(boolean reached) {

		Editor edit = app_prefs.edit();
		edit.putBoolean(Helper.KEY_DRIVER_REACHED, reached);
		edit.commit();

	}

	public boolean isDriverReached() {
		return app_prefs.getBoolean(Helper.KEY_DRIVER_REACHED, false);
	}
}
