package com.appdupe.uberforxserviceseeker;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.appdupe.uberforxserviceseeker.fragment.ETAFragment;
import com.appdupe.uberforxserviceseeker.fragment.EditAccount;
import com.appdupe.uberforxserviceseeker.fragment.FeedbackFragment;
import com.appdupe.uberforxserviceseeker.fragment.MyMapFragment;
import com.appdupe.uberforxserviceseeker.fragment.RateFragment;
import com.appdupe.uberforxserviceseeker.fragment.SettingFragment;
import com.appdupe.uberforxserviceseeker.helper.Helper;
import com.appdupe.uberforxserviceseeker.helper.JSONHelper;
import com.appdupe.uberforxserviceseeker.helper.PreferenceHelper;
import com.appdupe.uberforxserviceseeker.model.ClientRequest;

public class HomeActivity extends BaseActivity implements OnClickListener {
	private GlobalReceiver glbReceiver;
	private ImageButton btnLocateMe, btnAccount, btnAbout;
	private LinearLayout linearMain;
	// web view declare here because of back navigation problem and it used in
	// about fragment.
	public WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		linearMain = (LinearLayout) findViewById(R.id.linearMain);
		IntentFilter filter = new IntentFilter(Helper.ACTION_ETA_RECEIVED);
		glbReceiver = new GlobalReceiver();
		PreferenceHelper prefsHelper = new PreferenceHelper(this);
		LocalBroadcastManager.getInstance(this).registerReceiver(glbReceiver,
				filter);
		int id = getIntent().getIntExtra(Helper.EXTRA_ID, 1);

		if (savedInstanceState == null) {
			if (prefsHelper.getRequestStatus() && (id == 1 || id == 5)) {
				clearBackStack();
				ClientRequest clientRequest = new ClientRequest();
				clientRequest.setRandom_id(prefsHelper.getRequestID());

				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.home_container,
								ETAFragment.newInstance(clientRequest), "ETA")
						.commit();
			} else if (id == 2) {
				clearBackStack();
				ClientRequest clientRequest = new ClientRequest();
				clientRequest.setRandom_id(prefsHelper.getRequestID());

				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.home_container,
								RateFragment.newInstance(clientRequest),
								"RATE_FRAGMENT").commit();
			} else {
				clearBackStack();
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.home_container,
								MyMapFragment.newInstance(), "MAP_FRAGMENT")
						.commit();
			}
		}

		btnAccount = (ImageButton) findViewById(R.id.btn_account);
		btnLocateMe = (ImageButton) findViewById(R.id.btn_locate_me);
		btnAbout = (ImageButton) findViewById(R.id.btn_settings);
		btnAbout.setOnClickListener(this);
		btnLocateMe.setOnClickListener(this);
		btnAccount.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_account:
			EditAccount accFrag = (EditAccount) getSupportFragmentManager()
					.findFragmentByTag("ACCOUNT_FRAGMENT");

			if (accFrag != null && accFrag.isVisible()) {
				return;
			}
			clearBackStack();
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.addToBackStack(null);
			transaction.replace(R.id.home_container, EditAccount.newInstance(),
					"ACCOUNT_FRAGMENT").commit();
			break;
		case R.id.btn_settings:

			SettingFragment setttingFrag = (SettingFragment) getSupportFragmentManager()
					.findFragmentByTag("SETTING_FRAGMENT");

			if (setttingFrag != null && setttingFrag.isVisible()) {
				return;
			}
			clearBackStack();
			FragmentTransaction settingTrans = getSupportFragmentManager()
					.beginTransaction();
			settingTrans.addToBackStack(null);
			settingTrans.replace(R.id.home_container,
					SettingFragment.newInstance(), "SETTING_FRAGMENT").commit();
			break;
		case R.id.btn_locate_me:

			MyMapFragment locateMeFrag = (MyMapFragment) getSupportFragmentManager()
					.findFragmentByTag("MAP_FRAGMENT");
			ETAFragment etaFrag = (ETAFragment) getSupportFragmentManager()
					.findFragmentByTag("ETA");
			FeedbackFragment feedBack = (FeedbackFragment) getSupportFragmentManager()
					.findFragmentByTag("FEEDBACK");
			RateFragment rateFrag = (RateFragment) getSupportFragmentManager()
					.findFragmentByTag("RATE_FRAGMENT");

			if (locateMeFrag != null) {
				if (locateMeFrag.isVisible()) {
					return;
				} else {

					clearBackStack();
				}
			} else if (etaFrag != null) {
				if (etaFrag.isVisible()) {
					return;
				} else {
					clearBackStack();
				}
			} else if (feedBack != null) {
				if (feedBack.isVisible()) {
					return;
				} else {
					clearBackStack();
				}
			} else if (rateFrag != null) {
				if (rateFrag.isVisible()) {
					return;
				} else {
					clearBackStack();
				}
			}
			break;

		}
	}

	public void clearBackStack() {
		FragmentManager manager = getSupportFragmentManager();
		if (manager.getBackStackEntryCount() > 0) {
			FragmentManager.BackStackEntry first = manager
					.getBackStackEntryAt(0);
			manager.popBackStack(first.getId(),
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);

		// int id = getIntent().getIntExtra(Helper.EXTRA_ID, 1);
		//
		// System.out.println("On New Intent : " + id);
	}

	class GlobalReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String json = intent.getStringExtra(Helper.EXTRA_ETA_RECEIVED);

			int id = JSONHelper.getID(json);

			if (id == 4) {
				MyMapFragment mapFrag = (MyMapFragment) getSupportFragmentManager()
						.findFragmentByTag("MAP_FRAGMENT");

				if (mapFrag != null && mapFrag.isVisible()) {
					return;
				}
				clearBackStack();
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.home_container,
								MyMapFragment.newInstance(), "MAP_FRAGMENT")
						.commit();
			}
			clearAll();

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(glbReceiver);
	}

	public void selectLocateMe() {
		btnAccount.setImageResource(R.drawable.tab_account_icon);
		btnAbout.setImageResource(R.drawable.tab_about_icon);
		btnLocateMe.setImageResource(R.drawable.tab_icon_over);
	}

	public void selectAboutMe() {
		btnAccount.setImageResource(R.drawable.tab_account_icon);
		btnAbout.setImageResource(R.drawable.tab_icon_over);
		btnLocateMe.setImageResource(R.drawable.tab_location_me_icon);
	}

	public void selectAccount() {
		btnAccount.setImageResource(R.drawable.tab_icon_over);
		btnAbout.setImageResource(R.drawable.tab_about_icon);
		btnLocateMe.setImageResource(R.drawable.tab_location_me_icon);
	}

	public void setMainBackground(int id) {
		linearMain.setBackgroundResource(id);
	}

	private void clearBackStack(String tag) {
		FragmentManager fm = getSupportFragmentManager();
		fm.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (webView != null) {
			if (webView.canGoBack())
				webView.goBack();
			else
				super.onBackPressed();
		} else {

			super.onBackPressed();
		}
	}

	public void CancelNotification(Context ctx, int notifyId) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager nMgr = (NotificationManager) ctx
				.getSystemService(ns);
		nMgr.cancel(notifyId);
	}

	public void clearAll() {
		NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nMgr.cancelAll();
	}
}
