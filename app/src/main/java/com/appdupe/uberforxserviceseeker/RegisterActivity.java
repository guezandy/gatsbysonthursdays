package com.appdupe.uberforxserviceseeker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.appdupe.uberforxserviceseeker.helper.DBHelper;
import com.appdupe.uberforxserviceseeker.helper.Helper;
import com.appdupe.uberforxserviceseeker.helper.HttpRequest;
import com.appdupe.uberforxserviceseeker.helper.JSONHelper;
import com.appdupe.uberforxserviceseeker.helper.PreferenceHelper;
import com.appdupe.uberforxserviceseeker.helper.ReadFiles;
import com.appdupe.uberforxserviceseeker.helper.URL;
import com.appdupe.uberforxserviceseeker.model.BasicNameValuePair;
import com.appdupe.uberforxserviceseeker.model.CountryCode;
import com.appdupe.uberforxserviceseeker.model.User;

public class RegisterActivity extends BaseActivity implements OnClickListener,
		OnFocusChangeListener, OnCheckedChangeListener {

	private EditText et_Name, et_Email, et_Password, et_Mobile, et_DOB;
	private Button btn_country_code;

	private ProgressDialog pd;

	private String gender = "Male";
	private String country_code[];
	private ArrayList<CountryCode> list;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}

			if (msg.what == 1) {
				Helper.popToast(RegisterActivity.this, "Welcome "
						+ msg.obj.toString().trim());

				startActivity(new Intent(RegisterActivity.this,
						HomeActivity.class));
				RegisterActivity.this.finish();
			} else {
				Helper.popToast(RegisterActivity.this,
						"Register failed, Please try again");
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		et_Name = (EditText) findViewById(R.id.et_register_name);
		et_Email = (EditText) findViewById(R.id.et_register_email);
		et_Password = (EditText) findViewById(R.id.et_register_password);
		et_Mobile = (EditText) findViewById(R.id.et_register_contact);
		et_DOB = (EditText) findViewById(R.id.et_register_dob);
		btn_country_code = (Button) findViewById(R.id.btn_register_country_code);

		et_DOB.setOnFocusChangeListener(this);
		et_Email.setOnFocusChangeListener(this);
		et_Mobile.setOnFocusChangeListener(this);
		et_Name.setOnFocusChangeListener(this);
		et_Password.setOnFocusChangeListener(this);

		et_DOB.setOnClickListener(this);
		btn_country_code.setOnClickListener(this);

		((ToggleButton) findViewById(R.id.tb_register_sex))
				.setOnCheckedChangeListener(this);
		findViewById(R.id.btn_register_submit).setOnClickListener(this);

		String response = "";
		try {
			response = ReadFiles.readRawFileAsString(getApplicationContext(),
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

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setTitle(getResources().getString(R.string.register));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (isChecked) {
			gender = "Male";
		} else {
			gender = "Female";
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

		if (!hasFocus) {
			switch (v.getId()) {
			case R.id.et_register_email:
				validateEmail();
				break;

			case R.id.et_register_contact:
				validateView(et_Mobile, R.string.empty_mobile);
				break;

			case R.id.et_register_dob:
				validateView(et_DOB, R.string.empty_dob);
				break;

			case R.id.et_register_name:
				validateView(et_Name, R.string.empty_name);
				break;

			case R.id.et_register_password:
				validateView(et_Password, R.string.empty_password);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_register_submit:
			if (validateEmail()
					&& validateView(et_Mobile, R.string.empty_mobile)
					&& validateView(et_DOB, R.string.empty_dob)
					&& validateView(et_Name, R.string.empty_name)
					&& validateView(et_Password, R.string.empty_password)) {

				performRegister();
			} else {
				Helper.popToast(RegisterActivity.this, "Fields cannot be blank");
			}
			break;

		case R.id.et_register_dob:
			Calendar c = Calendar.getInstance();
			int mYear = c.get(Calendar.YEAR);
			int mMonth = c.get(Calendar.MONTH);
			int mDay = c.get(Calendar.DAY_OF_MONTH);
			new DatePickerDialog(RegisterActivity.this, dateSetListener, mYear,
					mMonth, mDay).show();
			break;

		case R.id.btn_register_country_code:
			AlertDialog.Builder dialog_countrycode = new Builder(
					RegisterActivity.this);
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
	};

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

	private OnDateSetListener dateSetListener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			et_DOB.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
		}
	};

	private void performRegister() {
		pd = Helper.showProgressDialog(this,
				getResources().getString(R.string.app_name), "Registering ...");

		final HttpRequest httpRequest = new HttpRequest();

		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("name", et_Name.getText()
						.toString().trim()));
				params.add(new BasicNameValuePair("email", et_Email.getText()
						.toString().trim()));
				params.add(new BasicNameValuePair("password", et_Password
						.getText().toString().trim()));
				params.add(new BasicNameValuePair("contact", et_Mobile
						.getText().toString().trim()));
				params.add(new BasicNameValuePair("date_of_birth", et_DOB
						.getText().toString().trim()));
				params.add(new BasicNameValuePair("country_code",
						btn_country_code.getText().toString().trim()));
				params.add(new BasicNameValuePair("gender", gender));
				params.add(new BasicNameValuePair("device_token",
						new PreferenceHelper(RegisterActivity.this)
								.getDeviceToken()));
				params.add(new BasicNameValuePair("device_type", "0"));

				try {
					String response = httpRequest
							.postData(URL.REGISTER, params);

					if (JSONHelper.getStatus(response)) {
						User user = JSONHelper.getUserDetails(response);

						new DBHelper(RegisterActivity.this).createUser(user);

						Message msg = mHandler.obtainMessage(1, user.getName());
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

	private boolean validateEmail() {
		if (!Helper.isEmailValid(et_Email.getText().toString().trim())) {
			et_Email.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.wrong, 0);
			et_Email.setError(getString(R.string.empty_email), null);

			return false;
		} else {
			et_Email.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.correct, 0);
			et_Email.setError(null, null);

			return true;
		}
	}

	private boolean validateView(EditText editView, int textid) {
		if (editView.getText().toString().trim().length() == 0) {
			editView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.wrong, 0);
			editView.setError(getString(textid), null);

			return false;
		} else {
			editView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.correct, 0);
			editView.setError(null, null);

			return true;
		}
	}
}
