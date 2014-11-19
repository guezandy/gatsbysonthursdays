package com.appdupe.uberforxserviceseeker.fragment;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appdupe.uberforxserviceseeker.HomeActivity;
import com.appdupe.uberforxserviceseeker.LoginActivity;
import com.appdupe.uberforxserviceseeker.R;
import com.appdupe.uberforxserviceseeker.helper.DBHelper;
import com.appdupe.uberforxserviceseeker.helper.Helper;
import com.appdupe.uberforxserviceseeker.helper.HttpRequest;
import com.appdupe.uberforxserviceseeker.helper.JSONHelper;
import com.appdupe.uberforxserviceseeker.helper.ReadFiles;
import com.appdupe.uberforxserviceseeker.helper.URL;
import com.appdupe.uberforxserviceseeker.model.BasicNameValuePair;
import com.appdupe.uberforxserviceseeker.model.CountryCode;
import com.appdupe.uberforxserviceseeker.model.User;

public class EditAccount extends Fragment implements OnClickListener {
	private EditText etName, etPhNo, etAge, etEmail;
	private Button btnSex;
	private Button btnUpdate, btnHistory, btnLogout, btn_country_code;
	private ProgressDialog pd;
	private ArrayList<CountryCode> list;

	private String country_code[];

	public static EditAccount newInstance() {
		EditAccount editAccount = new EditAccount();
		return editAccount;
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}

			if (msg.what == 1) {
				Helper.popToast(getActivity(), "Profile successfully updated ");

			} else {

			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_account, container,
				false);

		etName = (EditText) view.findViewById(R.id.etName);
		etEmail = (EditText) view.findViewById(R.id.etEmail);
		btnSex = (Button) view.findViewById(R.id.btnSex);
		etPhNo = (EditText) view.findViewById(R.id.etPhNo);
		etAge = (EditText) view.findViewById(R.id.etAge);
		btnHistory = (Button) view.findViewById(R.id.btnHistory);
		btnUpdate = (Button) view.findViewById(R.id.btnUpdate);
		btnLogout = (Button) view.findViewById(R.id.btnLogout);
		btn_country_code = (Button) view.findViewById(R.id.btn_country_code);
		btnLogout.setOnClickListener(this);
		btnUpdate.setOnClickListener(this);
		btnHistory.setOnClickListener(this);
		btn_country_code.setOnClickListener(this);

		String response = "";
		try {
			response = ReadFiles.readRawFileAsString(getActivity(),
					R.raw.countrycodes);
			list = parseCountryList(response);
			country_code = new String[list.size()];
			for (int i = 0; i < list.size(); i++)
				country_code[i] = list.get(i).getName() + "("
						+ list.get(i).getCode() + ")";
			btn_country_code.setText(list.get(0).getCode());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;

	}

	public ArrayList<CountryCode> parseCountryList(String response) {
		ArrayList<CountryCode> list = new ArrayList<CountryCode>();
		try {
			JSONArray array = new JSONArray(response);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				CountryCode code = new CountryCode();
				code.setName(object.getString("name"));
				code.setCode(object.getString("phone-code"));
				list.add(code);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
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
				R.string.text_myaccount));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		User user = new DBHelper(getActivity()).getUser();
		if (user != null) {
			etName.setText(user.getName());
			etEmail.setText(user.getEmail());
			btn_country_code.setText(user.getCountry_code());
			String age = calculateAge(user.getDob());
			if (!TextUtils.isEmpty(age))
				etAge.setText(age);

			if (!TextUtils.isEmpty(user.getGender())
					&& user.getGender().equalsIgnoreCase("female")) {
				btnSex.setBackgroundResource(R.drawable.sex_selector_female);
			} else {
				btnSex.setBackgroundResource(R.drawable.sex_selector_male);

			}

			etPhNo.setText(user.getContact());

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnUpdate:
			if (isValidate()) {
				updateProfile();
			}
			break;
		case R.id.btnHistory:
			HistoryFragment historyFrag = (HistoryFragment) getActivity()
					.getSupportFragmentManager().findFragmentByTag(
							"HISTORY_FRAGMENT");
			if (historyFrag != null && historyFrag.isVisible()) {
				return;
			}

			FragmentTransaction transaction = getActivity()
					.getSupportFragmentManager().beginTransaction();
			transaction.addToBackStack(null);
			transaction.replace(R.id.home_container,
					HistoryFragment.newInstance(), "HISTORY_FRAGMENT").commit();
			break;
		case R.id.btnLogout:
			Logout();
			break;
		case R.id.btn_country_code:
			AlertDialog.Builder dialog_countrycode = new Builder(getActivity());
			dialog_countrycode.setTitle("Country Code");
			dialog_countrycode.setItems(country_code,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int index) {
							btn_country_code.setText(list.get(index).getCode());
						}

					});
			dialog_countrycode.show();
			break;

		}
	}

	public String calculateAge(String strDob) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(df.parse(strDob));
			return String.valueOf(getAge(cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public int getAge(int _year, int _month, int _day) {

		GregorianCalendar cal = new GregorianCalendar();
		int y, m, d, a;

		y = cal.get(Calendar.YEAR);
		m = cal.get(Calendar.MONTH);
		d = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(_year, _month, _day);
		a = y - cal.get(Calendar.YEAR);
		if ((m < cal.get(Calendar.MONTH))
				|| ((m == cal.get(Calendar.MONTH)) && (d < cal
						.get(Calendar.DAY_OF_MONTH)))) {
			--a;
		}
		if (a < 0)
			throw new IllegalArgumentException("Age < 0");
		return a;
	}

	private void updateProfile() {
		pd = Helper.showProgressDialog(getActivity(), "Wait",
				"Updating Profile...");

		final HttpRequest httpRequest = new HttpRequest();

		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("user_id", String
						.valueOf(new DBHelper(getActivity()).getClientID())));
				params.add(new BasicNameValuePair("is_driver", "0"));
				params.add(new BasicNameValuePair("name", etName.getText()
						.toString()));
				params.add(new BasicNameValuePair("contact", etPhNo.getText()
						.toString()));
				params.add(new BasicNameValuePair("country_code",
						btn_country_code.getText().toString()));

				params.add(new BasicNameValuePair("device_type", "0"));

				try {
					String response = httpRequest.postData(URL.EDIT_PROFILE,
							params);

					if (JSONHelper.getStatus(response)) {
						User user = JSONHelper.getUserDetails(response);

						new DBHelper(getActivity()).createUser(user);

						Message msg = mHandler.obtainMessage(1, user);
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

	private boolean isValidate() {
		String msg = null;
		if (TextUtils.isEmpty(etName.getText().toString())) {
			msg = "Please enter user name";

		} else if (TextUtils.isEmpty(etPhNo.getText().toString())) {
			msg = "Please enter contact number";
		}
		if (msg == null)
			return true;
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
		return false;

	}

	private void Logout() {
		new DBHelper(getActivity()).deleteUser();
		new com.appdupe.uberforxserviceseeker.helper.PreferenceHelper(getActivity()).reset();
		startActivity(new Intent(getActivity(), LoginActivity.class));
		getActivity().finish();
	}
}
