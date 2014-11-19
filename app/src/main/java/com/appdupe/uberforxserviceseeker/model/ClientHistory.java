package com.appdupe.uberforxserviceseeker.model;

import java.io.Serializable;

/**
 * @author hardik a bhalodi
 * 
 */
@SuppressWarnings("serial")
public class ClientHistory implements Serializable {
	private String random_id;
	private String lattitude;
	private String logitude;
	private String client_id;
	private String driver_id;
	private String time_of_pickup;
	private String driver_name;
	private String client_name;
	private String end_logitude;
	private String end_lattitude;
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getEnd_logitude() {
		return end_logitude;
	}

	public void setEnd_logitude(String end_logitude) {
		this.end_logitude = end_logitude;
	}

	public String getEnd_lattitude() {
		return end_lattitude;
	}

	public void setEnd_lattitude(String end_lattitude) {
		this.end_lattitude = end_lattitude;
	}

	public String getRandom_id() {
		return random_id;
	}

	public void setRandom_id(String random_id) {
		this.random_id = random_id;
	}

	public String getLattitude() {
		return lattitude;
	}

	public void setLattitude(String lattitude) {
		this.lattitude = lattitude;
	}

	public String getLogitude() {
		return logitude;
	}

	public void setLogitude(String logitude) {
		this.logitude = logitude;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getDriver_id() {
		return driver_id;
	}

	public void setDriver_id(String driver_id) {
		this.driver_id = driver_id;
	}

	public String getTime_of_pickup() {
		return time_of_pickup;
	}

	public void setTime_of_pickup(String time_of_pickup) {
		this.time_of_pickup = time_of_pickup;
	}

	public String getDriver_name() {
		return driver_name;
	}

	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}

	public String getClient_name() {
		return client_name;
	}

	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}
}
