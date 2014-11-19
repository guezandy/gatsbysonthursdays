package com.appdupe.uberforxserviceseeker.helper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appdupe.uberforxserviceseeker.map.utils.PolyLineUtils;
import com.appdupe.uberforxserviceseeker.model.BeanRoute;
import com.appdupe.uberforxserviceseeker.model.BeanStep;
import com.appdupe.uberforxserviceseeker.model.ClientHistory;
import com.appdupe.uberforxserviceseeker.model.ClientRequest;
import com.appdupe.uberforxserviceseeker.model.DriverDetails;
import com.appdupe.uberforxserviceseeker.model.User;

public class JSONHelper {

	public static User getUserDetails(String json) {
		try {
			System.out.println(json);
			JSONObject jsonObject = new JSONObject(json);

			jsonObject = jsonObject.getJSONObject("uber_alpha").getJSONObject(
					"details");

			User user = new User();
			user.setClient_id(jsonObject.getInt("client_id"));
			user.setName(jsonObject.getString("name"));
			user.setGender(jsonObject.optString("gender"));
			user.setEmail(jsonObject.getString("email"));
			user.setContact(jsonObject.getString("contact"));
			user.setDob(jsonObject.getString("date_of_birth"));
			user.setReg_time(jsonObject.getString("reg_time"));
			user.setCountry_code(jsonObject.getString("country_code"));

			return user;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void getHistory(ArrayList<ClientHistory> list, String response) {
		try {
			System.out.println(response);
			JSONObject jsonObject = new JSONObject(response);

			JSONArray jsonArray = jsonObject.getJSONObject("uber_alpha")
					.getJSONArray("details");

			for (int i = 0; i < jsonArray.length(); i++) {
				ClientHistory history = new ClientHistory();
				JSONObject jHistory = jsonArray.getJSONObject(i);
				history.setLattitude(jHistory.getString("lattitude"));
				history.setLogitude(jHistory.getString("logitude"));
				history.setEnd_lattitude(jHistory.getString("end_lattitude"));
				history.setEnd_logitude(jHistory.getString("end_logitude"));
				history.setRandom_id(jHistory.getString("random_id"));
				history.setDriver_id(jHistory.getString("driver_id"));
				history.setTime_of_pickup(jHistory.getString("time_of_pickup"));
				history.setClient_id(jHistory.getString("client_id"));
				history.setDriver_name(jHistory.getString("driver_name"));
				history.setClient_name(jHistory.getString("client_name"));
				history.setDate(jHistory.getString("request_time"));
				list.add(history);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public static ClientRequest getClientRequest(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);

			jsonObject = jsonObject.getJSONObject("uber_alpha").getJSONObject(
					"details");

			ClientRequest clientRequest = new ClientRequest();

			clientRequest.setRequest_id(jsonObject.getInt("request_id"));
			clientRequest.setRandom_id(jsonObject.getString("random_id"));
			clientRequest.setRequest_time(jsonObject.getString("request_time"));
			clientRequest.setLat(jsonObject.getString("lattitude"));
			clientRequest.setLng(jsonObject.getString("logitude"));
			clientRequest.setClient_id(jsonObject.getInt("client_id"));
			clientRequest.setDriver_id(jsonObject.getInt("driver_id"));
			clientRequest.setTime_of_pickup(jsonObject
					.getString("time_of_pickup"));
			clientRequest
					.setRequest_status(jsonObject.getInt("request_status"));
			clientRequest.setComplete_status(jsonObject
					.getInt("complete_status"));
			clientRequest.setCancel_flag(jsonObject.getInt("cancel_flg"));

			return clientRequest;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static long getETAReceived(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject.getLong("time_of_pickup");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static String getRequestID(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject.getString("random_id");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean getStatus(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			String status = jsonObject.getJSONObject("uber_alpha").getString(
					"status");

			if (status.trim().equalsIgnoreCase("success")) {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static int getID(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject.getInt("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static BeanRoute parseRoute(String response, BeanRoute routeBean) {

		try {
			BeanStep stepBean;
			JSONObject jObject = new JSONObject(response);
			JSONArray jArray = jObject.getJSONArray("routes");
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject innerjObject = jArray.getJSONObject(i);
				if (innerjObject != null) {
					JSONArray innerJarry = innerjObject.getJSONArray("legs");
					for (int j = 0; j < innerJarry.length(); j++) {

						JSONObject jObjectLegs = innerJarry.getJSONObject(j);
						routeBean.setDistanceText(jObjectLegs.getJSONObject(
								"distance").getString("text"));
						routeBean.setDistanceValue(jObjectLegs.getJSONObject(
								"distance").getInt("value"));

						routeBean.setDurationText(jObjectLegs.getJSONObject(
								"duration").getString("text"));
						routeBean.setDurationValue(jObjectLegs.getJSONObject(
								"duration").getInt("value"));

						routeBean.setStartAddress(jObjectLegs
								.getString("start_address"));
						routeBean.setEndAddress(jObjectLegs
								.getString("end_address"));

						routeBean.setStartLat(jObjectLegs.getJSONObject(
								"start_location").getDouble("lat"));
						routeBean.setStartLon(jObjectLegs.getJSONObject(
								"start_location").getDouble("lng"));

						routeBean.setEndLat(jObjectLegs.getJSONObject(
								"end_location").getDouble("lat"));
						routeBean.setEndLon(jObjectLegs.getJSONObject(
								"end_location").getDouble("lng"));

						JSONArray jstepArray = jObjectLegs
								.getJSONArray("steps");
						if (jstepArray != null) {
							for (int k = 0; k < jstepArray.length(); k++) {
								stepBean = new BeanStep();
								JSONObject jStepObject = jstepArray
										.getJSONObject(k);
								if (jStepObject != null) {

									stepBean.setHtml_instructions(jStepObject
											.getString("html_instructions"));
									stepBean.setStrPoint(jStepObject
											.getJSONObject("polyline")
											.getString("points"));
									stepBean.setStartLat(jStepObject
											.getJSONObject("start_location")
											.getDouble("lat"));
									stepBean.setStartLon(jStepObject
											.getJSONObject("start_location")
											.getDouble("lng"));
									stepBean.setEndLat(jStepObject
											.getJSONObject("end_location")
											.getDouble("lat"));
									stepBean.setEndLong(jStepObject
											.getJSONObject("end_location")
											.getDouble("lng"));

									stepBean.setListPoints(new PolyLineUtils()
											.decodePoly(stepBean.getStrPoint()));
									routeBean.getListStep().add(stepBean);
								}

							}
						}
					}

				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return routeBean;
	}

	public static DriverDetails getDriverDetails(String json) {
		try {
			System.out.println(json);
			JSONObject jsonObject = new JSONObject(json);

			jsonObject = jsonObject.getJSONObject("uber_alpha");

			DriverDetails driver = new DriverDetails();
			driver.setUser_id(jsonObject.getString("user_id"));
			driver.setLattitude(jsonObject.getString("lattitude"));
			driver.setLogitude(jsonObject.getString("logitude"));
			driver.setContact(jsonObject.getString("contact"));
			driver.setName(jsonObject.getString("name"));
			return driver;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

}
