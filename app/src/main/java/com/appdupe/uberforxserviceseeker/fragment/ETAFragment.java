package com.appdupe.uberforxserviceseeker.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appdupe.uberforxserviceseeker.HomeActivity;
import com.appdupe.uberforxserviceseeker.R;
import com.appdupe.uberforxserviceseeker.helper.Helper;
import com.appdupe.uberforxserviceseeker.helper.HttpRequest;
import com.appdupe.uberforxserviceseeker.helper.JSONHelper;
import com.appdupe.uberforxserviceseeker.helper.LocationHelper;
import com.appdupe.uberforxserviceseeker.helper.PreferenceHelper;
import com.appdupe.uberforxserviceseeker.helper.TimerHelper;
import com.appdupe.uberforxserviceseeker.helper.URL;
import com.appdupe.uberforxserviceseeker.helper.LocationHelper.OnLocationReceived;
import com.appdupe.uberforxserviceseeker.helper.TimerHelper.OnTimeTick;
import com.appdupe.uberforxserviceseeker.model.BasicNameValuePair;
import com.appdupe.uberforxserviceseeker.model.ClientRequest;
import com.appdupe.uberforxserviceseeker.model.DriverDetails;
import com.appdupe.uberforxserviceseeker.model.LatLong;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ETAFragment extends Fragment implements OnClickListener,
		OnTimeTick, OnLocationReceived {
	private String strNumber = null;
	private ImageView ivClock;
	private Button btnMap, btnDetail, btnMMap, btnMDetail, btnCallDriver;
	private LinearLayout mapLayout, detailLayout;
	private GoogleMap mMap;
	private Marker markerDestination;
	private LatLong latlong;
	private Marker markerSource;
	private LocationHelper locationHelper;
	/** use for driver location tracking */
	private Timer timerTask;
	private boolean isComplete = false;

	private DriverDetails driver;

	public static ETAFragment newInstance(ClientRequest clientRequest) {
		ETAFragment etaFragment = new ETAFragment();

		Bundle extras = new Bundle();
		extras.putParcelable(Helper.EXTRA_CLIENT_REQUEST, clientRequest);
		etaFragment.setArguments(extras);

		return etaFragment;
	}

	private static final int INTERVAL = 1000;

	private TextView tvDesc;
	private LinearLayout timeLLLayout;

	private ETAReceiver etaReceiver;

	private PreferenceHelper prefHelper;
	private TimerHelper timeTick;
	private TextView txtHourOne, txtHourTwo, txtMinOne, txtMinTwo, txtSecOne,
			txtSecTwo, tvDriverName, tvDriverContact;

	private ClientRequest getClientRequest() {
		return getArguments().getParcelable(Helper.EXTRA_CLIENT_REQUEST);
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (!ETAFragment.this.isVisible()) {
					isComplete = true;
					return;
				}
				if (driver != null) {
					LatLng latLng = new LatLng(Double.parseDouble(driver
							.getLattitude()), Double.parseDouble(driver
							.getLogitude()));
					setSelectedDriverMarker(latLng);
					if (!TextUtils
							.isEmpty(tvDriverContact.getText().toString())) {
						tvDriverContact.setText(driver.getContact().toString());
					}
					if (!TextUtils.isEmpty(tvDriverName.getText().toString())) {
						tvDriverName.setText(driver.getName().toString());
					}
					System.out.println("Tacking=========> " + latLng.latitude
							+ "," + latLng.longitude);
				}
				isComplete = true;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationHelper = new LocationHelper(getActivity());
		locationHelper.setLocationReceivedLister(this);
		IntentFilter filter = new IntentFilter(Helper.ACTION_ETA_RECEIVED);
		etaReceiver = new ETAReceiver();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				etaReceiver, filter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_eta, container, false);

		// tv_ETA = (TextView) view.findViewById(R.id.tv_eta_time);
		ivClock = (ImageView) view.findViewById(R.id.imageView1);
		tvDesc = (TextView) view.findViewById(R.id.tv_desc);
		timeLLLayout = (LinearLayout) view.findViewById(R.id.timeLLLayout);
		txtHourOne = (TextView) view.findViewById(R.id.txtHourOne);
		txtHourTwo = (TextView) view.findViewById(R.id.txtHourTwo);
		txtMinOne = (TextView) view.findViewById(R.id.txtMinOne);
		txtMinTwo = (TextView) view.findViewById(R.id.txtMinTwo);
		txtSecOne = (TextView) view.findViewById(R.id.txtSecOne);
		txtSecTwo = (TextView) view.findViewById(R.id.txtSecTwo);
		btnMap = (Button) view.findViewById(R.id.btn_map);
		btnDetail = (Button) view.findViewById(R.id.btn_details);
		btnMDetail = (Button) view.findViewById(R.id.btnDetail);
		btnMMap = (Button) view.findViewById(R.id.btnMap);
		tvDriverName = (TextView) view.findViewById(R.id.driverName);
		tvDriverContact = (TextView) view.findViewById(R.id.driverContact);
		mapLayout = (LinearLayout) view.findViewById(R.id.mapLinearLayout);
		detailLayout = (LinearLayout) view
				.findViewById(R.id.detailLinearLayout);

		setUpMapIfNeeded();

		btnMap.setOnClickListener(this);
		btnDetail.setOnClickListener(this);
		btnMDetail.setOnClickListener(this);
		btnMMap.setOnClickListener(this);

		TextView tv_RefNO = (TextView) view.findViewById(R.id.tv_refno);
		tv_RefNO.setText(getClientRequest().getRandom_id());
		prefHelper = new PreferenceHelper(getActivity());
		view.findViewById(R.id.btn_call_operator).setOnClickListener(this);
		btnCallDriver = (Button) view.findViewById(R.id.btn_call_driver);
		btnCallDriver.setOnClickListener(this);

		showDetail();

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getOperator();
	}

	private void startThankYouFragment() {
		if (!this.isVisible())
			return;
		removeThisFragment();

		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.home_container,
						RateFragment.newInstance(getClientRequest()),
						"RATE_FRAGMENT").commitAllowingStateLoss();
	}

	private void startCountdown(long millis) {
		timeTick = new TimerHelper(millis, INTERVAL, ETAFragment.this);
		timeTick.start();
	}

	private void removeThisFragment() {
		try {
			getActivity().getSupportFragmentManager().beginTransaction()
					.remove(this).commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btn_call_operator) {
			if (!TextUtils.isEmpty(strNumber)) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + strNumber));
				startActivity(callIntent);
			} else {
				Toast.makeText(getActivity(), "Loading Operator Number",
						Toast.LENGTH_SHORT).show();
			}
		}
		if (v.getId() == R.id.btn_call_driver) {
			if (driver != null) {
				String number = driver.getContact();
				if (!TextUtils.isEmpty(number)) {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:" + number));
					startActivity(callIntent);
				} else {
					Toast.makeText(getActivity(),
							"Wait for getting x service provider contact",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getActivity(),
						"Wait for getting x service provider contact",
						Toast.LENGTH_SHORT).show();
			}
		}
		if (v.getId() == R.id.btn_map || v.getId() == R.id.btnMap) {
			showMap();
		}
		if (v.getId() == R.id.btn_details || v.getId() == R.id.btnDetail) {
			showDetail();
		}
	}

	@Override
	public void onTick(long millisUntilFinished, String time) {
		if (prefHelper.isDriverReached()) {
			timeTick.cancel();
			return;
		}
		if (TextUtils.isEmpty(time))
			return;
		tvDesc.setVisibility(View.GONE);
		ivClock.setVisibility(View.GONE);
		timeLLLayout.setVisibility(View.VISIBLE);
		tvDriverContact.setVisibility(View.VISIBLE);
		tvDriverName.setVisibility(View.VISIBLE);
		txtHourOne.setText(time.charAt(0) + "");
		txtHourTwo.setText(time.charAt(1) + "");
		txtMinOne.setText(time.charAt(2) + "");
		txtMinTwo.setText(time.charAt(3) + "");
		txtSecOne.setText(time.charAt(4) + "");
		txtSecTwo.setText(time.charAt(5) + "");

	}

	@Override
	public void onFinish() {
		prefHelper.putETATimeInMillis(-1);
		timeLLLayout.setVisibility(View.GONE);
		tvDriverContact.setVisibility(View.GONE);
		tvDriverName.setVisibility(View.GONE);

		tvDesc.setText(getString(R.string.timer_over));
		tvDesc.setVisibility(View.VISIBLE);
		ivClock.setVisibility(View.VISIBLE);
	}

	@Override
	public void onResume() {
		super.onResume();
		((HomeActivity) getActivity()).selectLocateMe();
		((HomeActivity) getActivity()).setMainBackground(R.drawable.bg_eta);
		((HomeActivity) getActivity())
				.setHeaderIcon(R.drawable.header_eta_icon);
		((HomeActivity) getActivity()).setTitle(getResources().getString(
				R.string.eta));
		((HomeActivity) getActivity()).actionBar.setTitle(getResources()
				.getString(R.string.eta));
		if (!prefHelper.isETAOver()) {
			startTrackingLocation();
		}
		if (!prefHelper.isETAOver() && !prefHelper.isDriverReached()) {
			startCountdown(prefHelper.getETATimeInMillis());
		}

		if (Helper.isInternetConnected(getActivity())
				&& !Helper.isGPSEnabled(getActivity())) {
			Helper.showLocationDialog(getActivity());
		} else {
			locationHelper.onStart();
			locationHelper.setLocationReceivedLister(this);
		}
	}

	@Override
	public void onDestroyView() {
		if (timeTick != null) {
			timeTick.cancel();
		}
		stopTrackingLocation();

		removeThisFragment();
		if (locationHelper != null) {
			locationHelper.onStop();
		}

		SupportMapFragment f = (SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.mapLocate);
		if (f != null) {
			try {
				getFragmentManager().beginTransaction().remove(f).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (markerSource != null) {
			markerSource.remove();
			markerSource = null;
		}
		if (markerDestination != null) {
			markerDestination.remove();
			markerDestination = null;

		}

		mMap = null;

		super.onDestroyView();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				etaReceiver);
	}

	class ETAReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String json = intent.getStringExtra(Helper.EXTRA_ETA_RECEIVED);

			int id = JSONHelper.getID(json);

			if (id == 1) {
				long eta = JSONHelper.getETAReceived(json);

				prefHelper.putETATimeInMillis(System.currentTimeMillis() + eta);
				startCountdown(eta);
				startTrackingLocation();
			} else if (id == 2) {
				prefHelper.putETATimeInMillis(-1);
				((HomeActivity) getActivity()).clearBackStack();
				startThankYouFragment();
			} else if (id == 5) {
				showDriverArrivedText();
			}
		}
	}

	private void getOperator() {
		strNumber = null;
		final HttpRequest httpRequest = new HttpRequest();

		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				try {
					String response = httpRequest.postData(URL.GET_OPERATOR,
							params);

					if (JSONHelper.getStatus(response)) {
						System.out.println(response);
						strNumber = new JSONObject(response)
								.getJSONObject("uber_alpha")
								.getJSONArray("details").getJSONObject(0)
								.getString("operator");

					} else {
						strNumber = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
					strNumber = null;

				}
			}
		}).start();
	}

	private void showMap() {

		if (mapLayout.getVisibility() != View.VISIBLE) {
			if (locationHelper != null) {
				locationHelper.onStart();
				locationHelper.setLocationReceivedLister(this);
			}
			detailLayout.setVisibility(View.GONE);
			mapLayout.setVisibility(View.VISIBLE);
		}
	}

	private void showDetail() {
		if (prefHelper.isDriverReached())
			showDriverArrivedText();
		if (detailLayout.getVisibility() != View.VISIBLE) {
			mapLayout.setVisibility(View.GONE);
			detailLayout.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onLocationReceived(LatLong latlong) {
		// TODO Auto-generated method stub
		if (mMap == null) {
			return;
		}

		this.latlong = latlong;
		locationHelper.setLocationReceivedLister(null);
		locationHelper.onStop();
		locateMe();
	}

	private void locateMe() {
		if (markerSource != null) {
			markerSource.remove();
		}

		markerSource = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(latlong.getLat(), latlong.getLng()))
				.title("My Position")
				.snippet(getString(R.string.i_am_here))
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(latlong.getLat(), latlong.getLng()),
				Helper.DEFAULT_ZOOM));

	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			mMap = ((SupportMapFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(
							R.id.mapLocate)).getMap();
			// Check if we were successful in obtaining the map.

			if (mMap != null) {
				Log.i("Map", "Map Fragment");
			}
		}
	}

	private void startTrackingLocation() {
		isComplete = true;
		timerTask = new Timer();
		timerTask.scheduleAtFixedRate(new TrackLocation(), new Date(), 3000);
	}

	private void stopTrackingLocation() {
		if (timerTask != null) {
			isComplete = true;
			timerTask.cancel();
		}
	}

	class TrackLocation extends TimerTask {

		public void run() {

			if (isComplete) {
				isComplete = false;
				getDriverLocation();
			}
		}
	}

	private void getDriverLocation() {

		final HttpRequest httpRequest = new HttpRequest();

		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("random_id", prefHelper
						.getRequestID() + ""));
				try {
					String response = httpRequest.postData(
							URL.GET_DRIVER_LOCATION, params);

					if (JSONHelper.getStatus(response)) {
						System.out.println(response);
						driver = JSONHelper.getDriverDetails(response);
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

	public void setSelectedDriverMarker(LatLng latLng) {

		if (latLng != null) {
			if (mMap != null && markerDestination == null) {

				markerDestination = mMap
						.addMarker(new MarkerOptions()
								.position(latLng)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_RED))
								.title("X Service Provider")
								.snippet("Name :" + driver.getName()));
				animateCameraToMarker(latLng);
			} else {
				markerDestination.setPosition(latLng);

			}

		}
	}

	private void animateCameraToMarker(LatLng latLng) {
		if (mMap != null && latLng != null) {
			CameraUpdate cameraUpdate = null;
			cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
					Helper.DEFAULT_ZOOM);
			if (cameraUpdate != null)
				mMap.animateCamera(cameraUpdate);
		}
	}

	private void showDriverArrivedText() {
		if (timeTick != null)
			timeTick.cancel();
		timeLLLayout.setVisibility(View.GONE);
		ivClock.setVisibility(View.VISIBLE);
		tvDesc.setVisibility(View.VISIBLE);
		tvDriverContact.setVisibility(View.GONE);
		tvDriverName.setVisibility(View.GONE);
		tvDesc.setText(getResources().getString(
				R.string.x_Service_Provider_reached_desc));
	}

}
