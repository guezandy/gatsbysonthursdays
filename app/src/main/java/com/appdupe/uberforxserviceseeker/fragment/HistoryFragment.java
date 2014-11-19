package com.appdupe.uberforxserviceseeker.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.appdupe.uberforxserviceseeker.HomeActivity;
import com.appdupe.uberforxserviceseeker.R;
import com.appdupe.uberforxserviceseeker.adapter.HistoryAdapter;
import com.appdupe.uberforxserviceseeker.helper.DBHelper;
import com.appdupe.uberforxserviceseeker.helper.Helper;
import com.appdupe.uberforxserviceseeker.helper.HttpRequest;
import com.appdupe.uberforxserviceseeker.helper.JSONHelper;
import com.appdupe.uberforxserviceseeker.helper.URL;
import com.appdupe.uberforxserviceseeker.model.BasicNameValuePair;
import com.appdupe.uberforxserviceseeker.model.ClientHistory;

public class HistoryFragment extends Fragment {
	private ListView listView;
	private HistoryAdapter adapter;
	private ArrayList<ClientHistory> listHistory;
	private ProgressDialog pd;

	public static HistoryFragment newInstance() {
		HistoryFragment historyfrag = new HistoryFragment();
		return historyfrag;
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}

			if (msg.what == 1) {
				adapter.notifyDataSetChanged();

			} else {

			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_history, container,
				false);
		listView = (ListView) view.findViewById(R.id.lvHistory);

		return view;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		listHistory = new ArrayList<ClientHistory>();
		adapter = new HistoryAdapter((HomeActivity) getActivity(), listHistory);
		listView.setAdapter(adapter);
		getHistory();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((HomeActivity) getActivity()).selectAccount();
		((HomeActivity) getActivity()).setMainBackground(R.drawable.bg_account);
		((HomeActivity) getActivity())
				.setHeaderIcon(R.drawable.header_account_icon);
		((HomeActivity) getActivity()).setTitle(getResources().getString(
				R.string.text_history));
	}

	private void getHistory() {
		listHistory.clear();
		pd = Helper.showProgressDialog(getActivity(), "Wait",
				"Getting History...");

		final HttpRequest httpRequest = new HttpRequest();

		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("user_id", String
						.valueOf(new DBHelper(getActivity()).getClientID())));
				params.add(new BasicNameValuePair("is_driver", "0"));

				try {
					String response = httpRequest.postData(URL.GET_HISTORY,
							params);

					if (JSONHelper.getStatus(response)) {
						JSONHelper.getHistory(listHistory, response);

						Message msg = mHandler.obtainMessage(1, listHistory);
						mHandler.sendMessage(msg);
					} else {
						mHandler.sendEmptyMessage(0);
					}
				} catch (Exception e) {
					e.printStackTrace();

					mHandler.sendEmptyMessage(0);
				}
			}
		}).start();
	}

}
