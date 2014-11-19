package com.appdupe.uberforxserviceseeker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.appdupe.uberforxserviceseeker.model.User;

public class DBAdapter {

	private static final String TAG = "[ DBAdapter ]";

	private static final String KEY_ROWID = "rowid";
	private static final String KEY_CLIENTID = "client_id";
	private static final String KEY_NAME = "name";
	private static final String KEY_GENDER = "gender";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_CONTACT = "contact";
	private static final String KEY_DOB = "date_of_birth";
	private static final String KEY_REG_TIME = "reg_time";
	private static final String KEY_COUNTRY_CODE = "country_code";

	// private static final String KEY_REQUEST_ID = "request_id";
	// private static final String KEY_DRIVER_ID = "driver_id";
	// private static final String KEY_REQUEST_STATUS = "request_status";
	// private static final String KEY_COMPLETE_STATUS = "complete_status";
	// private static final String KEY_CANCEL_FLAG = "cancel_flag";
	// private static final String KEY_RANDOM_ID = "random_id";
	// private static final String KEY_REQUEST_TIME = "request_time";
	// private static final String KEY_LAT = "lat";
	// private static final String KEY_LNG = "lng";
	// private static final String KEY_TIME_OF_PICKUP = "time_of_pickup";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "DBUberClient";

	private static final String USER_TABLE = "User";
	// private static final String REQUEST_TABLE = "ClientRequest";

	private static final String TABLE_CREATE_LIST = "create table "
			+ USER_TABLE + "( " + KEY_ROWID
			+ " integer primary key autoincrement," + KEY_CLIENTID
			+ " integer not null," + KEY_NAME + " text not null," + KEY_GENDER
			+ " text not null," + KEY_EMAIL + " text not null," + KEY_CONTACT
			+ " text not null," + KEY_DOB + " text not null," + KEY_REG_TIME
			+ " text not null," + KEY_COUNTRY_CODE + " text not null);";

	// private static final String TABLE_CLIENT_REQUEST = "create table "
	// + REQUEST_TABLE + "( " + KEY_ROWID
	// + " integer primary key autoincrement," + KEY_REQUEST_ID
	// + " integer not null," + KEY_CLIENTID + " integer not null,"
	// + KEY_DRIVER_ID + " integer not null," + KEY_REQUEST_STATUS
	// + " integer not null," + KEY_COMPLETE_STATUS + " integer not null,"
	// + KEY_CANCEL_FLAG + " integer not null," + KEY_RANDOM_ID
	// + " text not null," + KEY_REQUEST_TIME + " text not null,"
	// + KEY_LAT + " text not null," + KEY_LNG + " text not null,"
	// + KEY_TIME_OF_PICKUP + " text not null);";

	private DatabaseHelper DBhelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		DBhelper = new DatabaseHelper(ctx);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_CREATE_LIST);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}

	public DBAdapter open() throws SQLiteException {
		db = DBhelper.getWritableDatabase();
		return this;
	}

	public boolean isCreated() {
		if (db != null) {
			return db.isOpen();
		}

		return false;
	}

	public boolean isOpen() {
		return db.isOpen();
	}

	public void close() {
		DBhelper.close();
		db.close();
	}

	public long createUser(User user) {
		deleteUser();

		ContentValues values = new ContentValues();
		values.put(KEY_CLIENTID, user.getClient_id());
		values.put(KEY_NAME, user.getName());
		values.put(KEY_GENDER, user.getGender());
		values.put(KEY_EMAIL, user.getEmail());
		values.put(KEY_CONTACT, user.getContact());
		values.put(KEY_DOB, user.getDob());
		values.put(KEY_REG_TIME, user.getReg_time());
		values.put(KEY_COUNTRY_CODE, user.getCountry_code());

		return db.insert(USER_TABLE, null, values);
	}

	public User getUser() {
		Cursor cursor = db.query(USER_TABLE, new String[] { KEY_CLIENTID,
				KEY_NAME, KEY_GENDER, KEY_EMAIL, KEY_CONTACT, KEY_DOB,
				KEY_REG_TIME, KEY_COUNTRY_CODE }, null, null, null, null, null);
		if (cursor == null) {
			return null;
		}

		User user = new User();

		cursor.moveToFirst();

		user.setClient_id(cursor.getInt(0));
		user.setName(cursor.getString(1));
		user.setGender(cursor.getString(2));
		user.setEmail(cursor.getString(3));
		user.setContact(cursor.getString(4));
		user.setDob(cursor.getString(5));
		user.setReg_time(cursor.getString(6));
		user.setCountry_code(cursor.getString(7));

		cursor.close();

		return user;
	}

	public int deleteUser() {
		return db.delete(USER_TABLE, null, null);
	}

	public boolean isUserCreated() {
		Cursor cursor = db.query(USER_TABLE, new String[] { KEY_CLIENTID,
				KEY_NAME, KEY_EMAIL, KEY_CONTACT, KEY_DOB, KEY_REG_TIME,
				KEY_COUNTRY_CODE }, null, null, null, null, null);
		if (cursor == null) {
			return false;
		}

		cursor.moveToFirst();

		try {
			if (cursor.getCount() == 0) {
				return false;
			} else {
				return true;
			}
		} finally {
			cursor.close();
		}
	}

	public int getClientID() {
		Cursor cursor = db.query(USER_TABLE, new String[] { KEY_CLIENTID },
				null, null, null, null, null);
		if (cursor == null) {
			return -1;
		}

		cursor.moveToFirst();

		try {
			if (cursor.getCount() == 0) {
				return -1;
			} else {
				return cursor.getInt(0);
			}
		} finally {
			cursor.close();
		}
	}
}
