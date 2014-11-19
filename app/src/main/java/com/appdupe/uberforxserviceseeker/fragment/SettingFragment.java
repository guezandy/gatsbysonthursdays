package com.appdupe.uberforxserviceseeker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appdupe.uberforxserviceseeker.HomeActivity;
import com.appdupe.uberforxserviceseeker.R;
import com.appdupe.uberforxserviceseeker.helper.URL;

public class SettingFragment extends Fragment {
	private HomeActivity homeActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		homeActivity = (HomeActivity) getActivity();
	}

	public static SettingFragment newInstance() {
		SettingFragment settingFrag = new SettingFragment();
		return settingFrag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_settings, container,
				false);

		homeActivity.webView = (WebView) view.findViewById(R.id.webView);
		homeActivity.webView.getSettings().setJavaScriptEnabled(true);
		homeActivity.webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return false;
			}
		});
		// webView.getSettings().setSupportMultipleWindows(true);
		// webView.getSettings().setLoadWithOverviewMode(true);
		// webView.getSettings().setUseWideViewPort(true);
		homeActivity.webView.loadUrl(URL.GET_ABOUT);

		return view;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((HomeActivity) getActivity()).selectAboutMe();
		((HomeActivity) getActivity()).setTitle(getResources().getString(
				R.string.text_about));
		((HomeActivity) getActivity())
				.setHeaderIcon(R.drawable.header_about_icon);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		homeActivity.webView = null;
		super.onDestroyView();

	}

}
