package com.appdupe.uberforxserviceseeker.helper;

import android.content.Context;

import com.appdupe.uberforxserviceseeker.db.DBAdapter;
import com.appdupe.uberforxserviceseeker.model.User;

public class DBHelper {

	private DBAdapter dbAdapter;

	public DBHelper(Context context) {
		dbAdapter = new DBAdapter(context);
	}

	public void createUser(User user) {
		dbAdapter.open();

		dbAdapter.createUser(user);

		dbAdapter.close();
	}

	public void deleteUser() {
		dbAdapter.open();

		dbAdapter.deleteUser();
		dbAdapter.close();
	}

	public User getUser() {
		dbAdapter.open();

		User user = dbAdapter.getUser();

		dbAdapter.close();
		return user;
	}

	public boolean isUserCreated() {
		dbAdapter.open();

		try {
			return dbAdapter.isUserCreated();
		} finally {
			dbAdapter.close();
		}
	}

	public int getClientID() {
		dbAdapter.open();

		try {
			return dbAdapter.getClientID();
		} finally {
			dbAdapter.close();
		}
	}
}
