package com.appdupe.uberforxserviceseeker.helper;

public class URL {

	private static final String BASE_URL = "http://www.appdupe.info/uber_alpha/ws/";
	// private static final String BASE_URL =
	// "http://192.168.0.130/uber_alpha/ws/";

	public static final String REGISTER = BASE_URL + "client_register.php";
	public static final String LOGIN = BASE_URL + "login.php";
	public static final String PICK_REQUEST = BASE_URL
			+ "client_pick_request.php";
	public static final String EDIT_PROFILE = BASE_URL + "edit_profile.php";
	public static final String GET_HISTORY = BASE_URL + "get_history.php";
	public static final String GET_ABOUT = BASE_URL + "about_us.php";

	public static final String RATE_SERVICE = BASE_URL + "rate_trip.php";
	public static final String GET_OPERATOR = BASE_URL + "get_operator.php";
	public static final String GET_DRIVER_LOCATION = BASE_URL
			+ "get_driver_location.php";

}
