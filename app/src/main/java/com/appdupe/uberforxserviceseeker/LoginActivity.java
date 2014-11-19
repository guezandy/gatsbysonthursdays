package com.appdupe.uberforxserviceseeker;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.appdupe.uberforxserviceseeker.helper.DBHelper;
import com.appdupe.uberforxserviceseeker.helper.Helper;
import com.appdupe.uberforxserviceseeker.helper.HttpRequest;
import com.appdupe.uberforxserviceseeker.helper.JSONHelper;
import com.appdupe.uberforxserviceseeker.helper.PreferenceHelper;
import com.appdupe.uberforxserviceseeker.helper.URL;
import com.appdupe.uberforxserviceseeker.model.BasicNameValuePair;
import com.appdupe.uberforxserviceseeker.model.User;

public class LoginActivity extends BaseActivity implements OnClickListener,
		OnFocusChangeListener {

	private EditText et_Email, et_Password;

	private ProgressDialog pd;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}

			if (msg.what == 1) {
				Helper.popToast(LoginActivity.this, "Welcome "
						+ msg.obj.toString().trim());

				startActivity(new Intent(LoginActivity.this, HomeActivity.class));
				LoginActivity.this.finish();
			} else {
				Helper.popToast(LoginActivity.this,
						"Login failed, Please check your email & password");
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		actionBar.hide();

		et_Email = (EditText) findViewById(R.id.et_login_email);
		et_Password = (EditText) findViewById(R.id.et_login_password);

		et_Email.setOnFocusChangeListener(this);
		et_Password.setOnFocusChangeListener(this);

		findViewById(R.id.btn_login_submit).setOnClickListener(this);
		findViewById(R.id.btn_login_register).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_login_submit:
			if (validateEmail() && validatePassword()) {
				performLogin(et_Email.getText().toString().trim(), et_Password
						.getText().toString().trim());
			}
			break;

		case R.id.btn_login_register:
			startActivity(new Intent(this, RegisterActivity.class));
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setTitle(getResources().getString(R.string.app_name));

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

		if (!hasFocus) {
			switch (v.getId()) {
			case R.id.et_login_email:
				validateEmail();
				break;

			case R.id.et_login_password:
				validatePassword();
				break;
			}
		}
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

	private boolean validatePassword() {
		if (et_Password.getText().toString().trim().length() == 0) {
			et_Password.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.wrong, 0);
			et_Password.setError(getString(R.string.empty_password), null);

			return false;
		} else {
			et_Password.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.correct, 0);
			et_Password.setError(null, null);

			return true;
		}
	}

	private void performLogin(final String email, final String password) {
		pd = Helper.showProgressDialog(this,
				getResources().getString(R.string.app_name), "Logging in ...");

		final HttpRequest httpRequest = new HttpRequest();

		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("email", email));
				params.add(new BasicNameValuePair("password", password));
				params.add(new BasicNameValuePair("device_token",
						new PreferenceHelper(LoginActivity.this)
								.getDeviceToken()));
				params.add(new BasicNameValuePair("device_type", "0"));

				try {
					String response = httpRequest.postData(URL.LOGIN, params);

					if (JSONHelper.getStatus(response)) {
						User user = JSONHelper.getUserDetails(response);

						new DBHelper(LoginActivity.this).createUser(user);

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
}
