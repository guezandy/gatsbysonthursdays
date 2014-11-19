package com.appdupe.uberforxserviceseeker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ClientRequest implements Parcelable {

	private int request_id, client_id, driver_id, request_status,
			complete_status, cancel_flag;
	private String random_id, request_time, lat, lng, time_of_pickup;

	public ClientRequest() {
	}

	public ClientRequest(Parcel in) {
		request_id = in.readInt();
		client_id = in.readInt();
		driver_id = in.readInt();
		request_status = in.readInt();
		complete_status = in.readInt();
		cancel_flag = in.readInt();
		random_id = in.readString();
		request_time = in.readString();
		lat = in.readString();
		lng = in.readString();
		time_of_pickup = in.readString();
	}

	public static final Parcelable.Creator<ClientRequest> CREATOR = new Parcelable.Creator<ClientRequest>() {
		public ClientRequest createFromParcel(Parcel in) {
			return new ClientRequest(in);
		}

		public ClientRequest[] newArray(int size) {
			return new ClientRequest[size];
		}
	};

	public int getRequest_id() {
		return request_id;
	}

	public void setRequest_id(int request_id) {
		this.request_id = request_id;
	}

	public int getClient_id() {
		return client_id;
	}

	public void setClient_id(int client_id) {
		this.client_id = client_id;
	}

	public int getDriver_id() {
		return driver_id;
	}

	public void setDriver_id(int driver_id) {
		this.driver_id = driver_id;
	}

	public int getRequest_status() {
		return request_status;
	}

	public void setRequest_status(int request_status) {
		this.request_status = request_status;
	}

	public int getComplete_status() {
		return complete_status;
	}

	public void setComplete_status(int complete_status) {
		this.complete_status = complete_status;
	}

	public int getCancel_flag() {
		return cancel_flag;
	}

	public void setCancel_flag(int cancel_flag) {
		this.cancel_flag = cancel_flag;
	}

	public String getRandom_id() {
		return random_id;
	}

	public void setRandom_id(String random_id) {
		this.random_id = random_id;
	}

	public String getRequest_time() {
		return request_time;
	}

	public void setRequest_time(String request_time) {
		this.request_time = request_time;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getTime_of_pickup() {
		return time_of_pickup;
	}

	public void setTime_of_pickup(String time_of_pickup) {
		this.time_of_pickup = time_of_pickup;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeInt(request_id);
		dest.writeInt(client_id);
		dest.writeInt(request_status);
		dest.writeInt(complete_status);
		dest.writeInt(cancel_flag);
		dest.writeString(random_id);
		dest.writeString(request_time);
		dest.writeString(lat);
		dest.writeString(lng);
		dest.writeString(time_of_pickup);
	}
}
