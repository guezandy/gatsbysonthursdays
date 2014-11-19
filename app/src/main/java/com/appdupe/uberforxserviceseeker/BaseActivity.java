package com.appdupe.uberforxserviceseeker;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.appdupe.uberforxserviceseeker.helper.Helper;

public class BaseActivity extends ActionBarActivity {
	public ActionBar actionBar;

	private TextView tvActionBar;
	private ImageView ivHeaderIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		actionBar = getSupportActionBar();
		actionBar.show();
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.actionbarlayout, null);
		tvActionBar = (TextView) v.findViewById(R.id.titleText);
		ivHeaderIcon = (ImageView) v.findViewById(R.id.ivHeaderIcon);
		LayoutParams layout = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(v, layout);

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!Helper.isInternetConnected(this)) {
			Helper.showNetworkDialog(this);
		}
	}

	public void setTitle(String title) {
		tvActionBar.setText(title);

	}

	public void setHeaderIcon(int id) {
		ivHeaderIcon.setImageResource(id);
	}
}
