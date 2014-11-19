package com.appdupe.uberforxserviceseeker.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.appdupe.uberforxserviceseeker.HomeActivity;
import com.appdupe.uberforxserviceseeker.R;
import com.appdupe.uberforxserviceseeker.helper.Helper;
import com.appdupe.uberforxserviceseeker.helper.HttpRequest;
import com.appdupe.uberforxserviceseeker.helper.JSONHelper;
import com.appdupe.uberforxserviceseeker.helper.URL;
import com.appdupe.uberforxserviceseeker.model.BasicNameValuePair;
import com.appdupe.uberforxserviceseeker.model.ClientRequest;

public class RateFragment extends Fragment implements OnClickListener,OnRatingBarChangeListener {

	public static RateFragment newInstance(ClientRequest clientRequest) {
		RateFragment rateFragment = new RateFragment();

		Bundle extras = new Bundle();
		extras.putParcelable(Helper.EXTRA_CLIENT_REQUEST, clientRequest);
		rateFragment.setArguments(extras);

		return rateFragment;
	}

	 private RatingBar ratebar;

	private ImageButton btnRate1, btnRate2, btnRate3, btnRate4, btnRate5;
	private EditText et_Comments;

	private ProgressDialog pd;

	private float rating;

	private ClientRequest getClientRequest() {
		return getArguments().getParcelable(Helper.EXTRA_CLIENT_REQUEST);
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}

			if (msg.what == 1) {
				Helper.popToast(getActivity(), getString(R.string.rate_success));

				removeNotification();
				removeThisFragment();

				getActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.home_container,
								MyMapFragment.newInstance(), "MAP_FRAGMENT")
						.commit();
			} else {
				Helper.popToast(getActivity(), getString(R.string.rate_failed));
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_rate, container, false);
//		btnRate1 = (ImageButton) view.findViewById(R.id.imageButton1);
//		btnRate2 = (ImageButton) view.findViewById(R.id.imageButton2);
//		btnRate3 = (ImageButton) view.findViewById(R.id.imageButton3);
//		btnRate4 = (ImageButton) view.findViewById(R.id.imageButton4);
//		btnRate5 = (ImageButton) view.findViewById(R.id.imageButton5);
//		btnRate1.setOnClickListener(this);
//		btnRate2.setOnClickListener(this);
//		btnRate3.setOnClickListener(this);
//		btnRate4.setOnClickListener(this);
//		btnRate5.setOnClickListener(this);
//		btnRate1.setColorFilter(Color.argb(127, 0, 0, 0));
//		btnRate2.setColorFilter(Color.argb(127, 0, 0, 0));
//		btnRate3.setColorFilter(Color.argb(127, 0, 0, 0));
//		btnRate4.setColorFilter(Color.argb(127, 0, 0, 0));
//		btnRate5.setColorFilter(Color.argb(127, 0, 0, 0));

		ratebar = (RatingBar) view.findViewById(R.id.ratebar_client);
		et_Comments = (EditText) view.findViewById(R.id.et_comments);

		ratebar.setOnRatingBarChangeListener(this);

		view.findViewById(R.id.btn_comments).setOnClickListener(this);

		TextView tv_RefNO = (TextView) view.findViewById(R.id.tv_rate_refno);
		tv_RefNO.setText(getClientRequest().getRandom_id());

		return view;
	}

	private void performRateDriver() {
		pd = Helper.showProgressDialog(getActivity(), "Feedback..",
				"Updating your feedback ...");

		final HttpRequest httpRequest = new HttpRequest();

		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("random_id",
						getClientRequest().getRandom_id() + ""));
				params.add(new BasicNameValuePair("comment", et_Comments
						.getText().toString().trim()
						+ ""));
				params.add(new BasicNameValuePair("rating", rating + ""));

				try {
					String response = httpRequest.postData(URL.RATE_SERVICE,
							params);

					if (JSONHelper.getStatus(response)) {
						mHandler.sendEmptyMessage(1);
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

	private void removeNotification() {
		NotificationManager mNotificationManager = (NotificationManager) getActivity()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(0);
	}

	private void removeThisFragment() {
		try {
			getActivity().getSupportFragmentManager().beginTransaction()
					.remove(this).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((HomeActivity) getActivity()).selectLocateMe();
		((HomeActivity) getActivity()).setTitle(getResources().getString(
				R.string.text_feedback));
		((HomeActivity) getActivity())
				.setHeaderIcon(R.drawable.header_feedback_icon);
		((HomeActivity) getActivity())
				.setMainBackground(R.drawable.bg_feedback);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_comments:
			performRateDriver();
			break;
		/*case R.id.imageButton1:
			this.rating = 1.0f;
			btnRate1.setColorFilter(Color.argb(0, 0, 0, 0));
			btnRate2.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate3.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate4.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate5.setColorFilter(Color.argb(127, 0, 0, 0));
			break;
		case R.id.imageButton2:
			this.rating = 2.0f;
			btnRate1.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate2.setColorFilter(Color.argb(0, 0, 0, 0));
			btnRate3.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate4.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate5.setColorFilter(Color.argb(127, 0, 0, 0));
			break;
		case R.id.imageButton3:
			this.rating = 3.0f;
			btnRate1.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate2.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate3.setColorFilter(Color.argb(0, 0, 0, 0));
			btnRate4.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate5.setColorFilter(Color.argb(127, 0, 0, 0));
			break;
		case R.id.imageButton4:
			this.rating = 4.0f;
			btnRate1.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate2.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate3.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate4.setColorFilter(Color.argb(0, 0, 0, 0));
			btnRate5.setColorFilter(Color.argb(127, 0, 0, 0));
			break;
		case R.id.imageButton5:
			this.rating = 5.0f;
			btnRate1.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate2.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate3.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate4.setColorFilter(Color.argb(127, 0, 0, 0));
			btnRate5.setColorFilter(Color.argb(0, 0, 0, 0));
			break;*/

		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		removeThisFragment();
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		this.rating=rating;
		Log.i("Rating", ""+rating);
		
	}
}
