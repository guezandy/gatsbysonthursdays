package com.appdupe.uberforxserviceseeker.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.appdupe.uberforxserviceseeker.R;
import com.appdupe.uberforxserviceseeker.helper.DBHelper;
import com.appdupe.uberforxserviceseeker.helper.Helper;
import com.appdupe.uberforxserviceseeker.helper.HttpRequest;
import com.appdupe.uberforxserviceseeker.helper.JSONHelper;
import com.appdupe.uberforxserviceseeker.helper.LocationHelper;
import com.appdupe.uberforxserviceseeker.helper.PreferenceHelper;
import com.appdupe.uberforxserviceseeker.helper.URL;
import com.appdupe.uberforxserviceseeker.helper.LocationHelper.OnLocationReceived;
import com.appdupe.uberforxserviceseeker.model.BasicNameValuePair;
import com.appdupe.uberforxserviceseeker.model.ClientRequest;
import com.appdupe.uberforxserviceseeker.model.LatLong;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocateMeFragment extends Fragment implements OnLocationReceived,
		OnClickListener {

	public static LocateMeFragment newInstance() {
		LocateMeFragment mapFragment = new LocateMeFragment();
		return mapFragment;
	}

	private GoogleMap mMap;
	private Marker myPosition;

	private LocationHelper locationHelper;

	private LatLong latlong;
	private ClientRequest clientRequest;

	private ProgressDialog pd;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}

			if (msg.what == 1) {
				Helper.popToast(getActivity(),
						getString(R.string.client_request_success));

				getActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.home_container,
								ETAFragment.newInstance(clientRequest))
						.commit();

				new PreferenceHelper(getActivity())
						.putClientRequestActive(true);
			} else {
				Helper.popToast(getActivity(),
						getString(R.string.client_request_failed));
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_locate, container, false);

		setUpMapIfNeeded();
		return view;
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

	private void performPickUpRequest() {
		pd = Helper.showProgressDialog(getActivity(), "Requesting..",
				"Notifying driver");

		final HttpRequest httpRequest = new HttpRequest();

		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("lattitude", latlong.getLat()
						+ ""));
				params.add(new BasicNameValuePair("logitude", latlong.getLng()
						+ ""));
				params.add(new BasicNameValuePair("client_id", new DBHelper(
						getActivity()).getClientID() + ""));

				try {
					String response = httpRequest.postData(URL.PICK_REQUEST,
							params);

					if (JSONHelper.getStatus(response)) {
						clientRequest = JSONHelper.getClientRequest(response);

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

	private void locateMe() {
		if (myPosition != null) {
			myPosition.remove();
		}

		myPosition = mMap.addMarker(new MarkerOptions().position(
				new LatLng(latlong.getLat(), latlong.getLng())).title(
				getString(R.string.i_am_here)));

		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(latlong.getLat(), latlong.getLng()),
				Helper.DEFAULT_ZOOM));
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onLocationReceived(LatLong latlong) {
		if (mMap == null) {
			return;
		}

		this.latlong = latlong;

		locateMe();
		locationHelper.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (Helper.isInternetConnected(getActivity())
				&& !Helper.isGPSEnabled(getActivity())) {
			Helper.showLocationDialog(getActivity());
		} else {
			locationHelper = new LocationHelper(getActivity());
			locationHelper.setLocationReceivedLister(this);
			locationHelper.onStart();
		}
	}

	@Override
	public void onDestroyView() {
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

		if (myPosition != null) {
			myPosition.remove();
			myPosition = null;
		}

		mMap = null;

		super.onDestroyView();
	}
}
