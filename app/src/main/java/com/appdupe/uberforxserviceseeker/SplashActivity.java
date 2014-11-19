package com.appdupe.uberforxserviceseeker;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appdupe.uberforxserviceseeker.gcm.CommonUtilities;
import com.appdupe.uberforxserviceseeker.helper.DBHelper;
import com.appdupe.uberforxserviceseeker.helper.Helper;
import com.appdupe.uberforxserviceseeker.helper.PreferenceHelper;
import com.google.android.gcm.GCMRegistrar;

public class SplashActivity extends FragmentActivity {

	private GCMRegistration gcmRegistration;

	private ProgressDialog pd;

	private boolean hasRegistered = true;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}

			if (new DBHelper(SplashActivity.this).isUserCreated()) {
				startActivity(new Intent(SplashActivity.this,
						HomeActivity.class));
				SplashActivity.this.finish();
			} else {
				startActivity(new Intent(SplashActivity.this,
						LoginActivity.class));
				SplashActivity.this.finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		if (Helper.isInternetConnected(this)) {
			gcmRegistration = new GCMRegistration();
			LocalBroadcastManager.getInstance(this).registerReceiver(
					gcmRegistration,
					new IntentFilter(Helper.ACTION_GCM_TOKEN_RECEIVED));

			if (!new PreferenceHelper(this).isDeviceTokenAquired()) {
				registerGCM();
			} else {
				hasRegistered = false;
			}

			new Thread(new Runnable() {

				@Override
				public void run() {
					while (hasRegistered) {
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					mHandler.sendEmptyMessage(1);
				}
			}).start();
		} else {
			Helper.showNetworkDialog(this);
		}
	}

	private void registerGCM() {

		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		String gcm_id = GCMRegistrar.getRegistrationId(this);

		if (gcm_id.equals("")) {
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		} else {
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
			gcm_id = GCMRegistrar.getRegistrationId(this);
		}

		if (!gcm_id.equals("")) {
			new PreferenceHelper(this).putDeviceToken(gcm_id);
		} else {
			pd = Helper.showProgressDialog(this,
					getResources().getString(R.string.app_name),
					"Registering Device ...");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				gcmRegistration);
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.splash, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // as you specify a parent activity in AndroidManifest.xml.
	// int id = item.getItemId();
	// if (id == R.id.action_settings) {
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_splash,
					container, false);
			return rootView;
		}
	}

	public class GCMRegistration extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			hasRegistered = false;
		}
	}
}
