package com.appdupe.uberforxserviceseeker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

	private int client_id;
	private String name, email, contact, dob, reg_time, gender,country_code;


	public User() {
	}

	public User(Parcel in) {
		client_id = in.readInt();
		name = in.readString();
		email = in.readString();
		contact = in.readString();
		dob = in.readString();
		reg_time = in.readString();
		gender = in.readString();
		country_code=in.readString();
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getClient_id() {
		return client_id;
	}

	public void setClient_id(int client_id) {
		this.client_id = client_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getReg_time() {
		return reg_time;
	}

	public void setReg_time(String reg_time) {
		this.reg_time = reg_time;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(client_id);
		dest.writeString(name);
		dest.writeString(email);
		dest.writeString(contact);
		dest.writeString(dob);
		dest.writeString(reg_time);
		dest.writeString(gender);
		dest.writeString(country_code);
	}
}
