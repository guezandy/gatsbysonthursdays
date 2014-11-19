package com.appdupe.uberforxserviceseeker.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appdupe.uberforxserviceseeker.HomeActivity;
import com.appdupe.uberforxserviceseeker.R;
import com.appdupe.uberforxserviceseeker.helper.Helper;
import com.appdupe.uberforxserviceseeker.helper.HttpRequest;
import com.appdupe.uberforxserviceseeker.helper.JSONHelper;
import com.appdupe.uberforxserviceseeker.model.BeanRoute;
import com.appdupe.uberforxserviceseeker.model.BeanStep;
import com.appdupe.uberforxserviceseeker.model.ClientHistory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class HistoryDetailFragment extends Fragment implements OnClickListener {
	private ClientHistory history;
	private GoogleMap mMap;

	private PolylineOptions lineOptions;
	private BeanRoute route;
	ArrayList<LatLng> points;
	private ProgressDialog pd;

	public static HistoryDetailFragment newInstance() {
		HistoryDetailFragment histiryDetail = new HistoryDetailFragment();
		return histiryDetail;
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}

			if (msg.what == 1) {
				mMap.addMarker(new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_RED))
						.position(
								new LatLng(Double.parseDouble(history
										.getLattitude()), Double
										.parseDouble(history.getLogitude())))
						.title(getString(R.string.i_am_here)));

				mMap.addMarker(new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
						.position(
								new LatLng(Double.parseDouble(history
										.getEnd_lattitude()), Double
										.parseDouble(history.getEnd_logitude())))
						.title(getString(R.string.X_Service_Provider_here)));

				lineOptions.addAll(points);
				lineOptions.width(5);
				lineOptions.color(0xff669900);
				mMap.addPolyline(lineOptions);

				LatLngBounds.Builder bld = new LatLngBounds.Builder();

				bld.include(new LatLng(Double.parseDouble(history
						.getLattitude()), Double.parseDouble(history
						.getLogitude())));
				bld.include(new LatLng(Double.parseDouble(history
						.getEnd_lattitude()), Double.parseDouble(history
						.getEnd_logitude())));
				LatLngBounds latLngBounds = bld.build();
				mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
						latLngBounds, 50));
			} else {

			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		history = (ClientHistory) getArguments().getSerializable("history");
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
				R.string.text_history_detail));
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			mMap = ((SupportMapFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(R.id.themap))
					.getMap();
			showDirection(
					new LatLng(Double.parseDouble(history.getLattitude()),
							Double.parseDouble(history.getLogitude())),
					new LatLng(Double.parseDouble(history.getEnd_lattitude()),
							Double.parseDouble(history.getEnd_logitude())));
			// Check if we were successful in obtaining the map.

			if (mMap != null) {
				Log.i("Map", "Map Fragment");
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater
				.inflate(R.layout.fragment_detail, container, false);

		((TextView) view.findViewById(R.id.txtDetailClient)).setText(history
				.getClient_name());
		((TextView) view.findViewById(R.id.txtDetailDriver)).setText(history
				.getDriver_name());
		((TextView) view.findViewById(R.id.txtDetailRefNo)).setText(history
				.getRandom_id());
		((TextView) view.findViewById(R.id.txtDetailPickUpTime))
				.setText(history.getDate().split(" ")[0].toString() + " "
						+ formatTime(history.getTime_of_pickup()));

		return view;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setUpMapIfNeeded();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	private String formatTime(String millisecond) {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.parseLong(millisecond));
		return sdf.format(cal.getTime());

	}

	private void showDirection(LatLng source, LatLng destination) {
		pd = Helper.showProgressDialog(getActivity(), "Wait",
				"Getting Direction...");
		final HttpRequest httpRequest = new HttpRequest();
		final String url = "http://maps.googleapis.com/maps/api/directions/json?origin="
				+ source.latitude
				+ ","
				+ source.longitude
				+ "&destination="
				+ destination.latitude
				+ ","
				+ destination.longitude
				+ "&sensor=false";
		new Thread(new Runnable() {

			@Override
			public void run() {
				//
				try {
					String response = httpRequest.sendGet(url);

					if (!TextUtils.isEmpty(response)) {
						route = new BeanRoute();
						JSONHelper.parseRoute(response, route);

						final ArrayList<BeanStep> step = route.getListStep();
						System.out.println("step size=====> " + step.size());
						points = new ArrayList<LatLng>();
						lineOptions = new PolylineOptions();

						for (int i = 0; i < step.size(); i++) {

							List<LatLng> path = step.get(i).getListPoints();
							System.out.println("step =====> " + i + " and "
									+ path.size());
							points.addAll(path);

						}
						Message msg = mHandler.obtainMessage(1, lineOptions);
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

	public void onDestroyView() {
		super.onDestroyView();
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.themap));
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}

}
