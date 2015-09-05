package silent.kuasapmaterial.libs;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.params.ClientPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import silent.kuasapmaterial.R;
import silent.kuasapmaterial.callback.BusCallback;
import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.callback.SemesterCallback;
import silent.kuasapmaterial.callback.ServerStatusCallback;
import silent.kuasapmaterial.callback.UserInfoCallback;
import silent.kuasapmaterial.models.BusModel;
import silent.kuasapmaterial.models.SemesterModel;
import silent.kuasapmaterial.models.ServerStatusModel;
import silent.kuasapmaterial.models.UserInfoModel;

/**
 * Created by HearSilent on 15/8/5.
 * Fetch KUAS AP data.
 */

public class Helper {

	private static final AsyncHttpClient mClient = init();

	private static AsyncHttpClient init() {
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Connection", "Keep-Alive");
		client.setTimeout(30 * 1000);
		client.getHttpClient().getParams()
				.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		return client;
	}

	public static final String SERVER_HOST = "kuas.grd.idv.tw";
	public static final int SERVER_PORT = 14769;
	public static final String BASE_URL = "https://" + SERVER_HOST + ":" + SERVER_PORT;
	public static final String BACKUP_URL = "http://api.grd.idv.tw:14768";

	public static final String SERVER_STATUS_URL = BASE_URL + "/latest/servers/status";
	public static final String APP_VERSION_URL = BASE_URL + "/latest/versions/android";
	public static final String LOGIN_URL = BASE_URL + "/latest/token";
	public static final String LOGOUT_URL = BASE_URL + "/ap/logout";
	public static final String CHECK_LOGIN_URL = BASE_URL + "/ap/is_login";
	public static final String SEMESTER_URL = BASE_URL + "/latest/ap/semester";
	public static final String AP_QUERY_URL = BASE_URL + "/ap/query";
	public static final String USER_INFO_URL = BASE_URL + "/ap/user/info";
	public static final String USER_PIC_URL = BASE_URL + "/ap/user/picture";
	public static final String LEAVE_QUERY_URL = BASE_URL + "/leave";
	public static final String LEAVE_SUBMIT_URL = BASE_URL + "/leave/submit";
	public static final String BUS_QUERY_URL = BASE_URL + "/latest/bus/timetables";
	public static final String BUS_RESERVE_URL = BASE_URL + "/bus/reserve";
	public static final String BUS_BOOKING_URL = BASE_URL + "/bus/booking";
	public static final String NOTIFICATION_URL = BASE_URL + "/notification/%s";
	public static final String NEWS_URL = BASE_URL + "/news";
	public static final String NEWS_STATUS_URL = BASE_URL + "/news/status";

	private static void onHelperFail(Context context, GeneralCallback callback, Exception e) {
		if (callback != null) {
			callback.onFail(context.getString(R.string.something_error) + "：" + e.toString());
		}
	}

	private static void onHelperFail(Context context, GeneralCallback callback, int statusCode,
	                                 Header[] headers) {
		onHelperFail(context, callback, statusCode, headers, null, (String) null);
	}

	private static void onHelperFail(Context context, GeneralCallback callback, int statusCode,
	                                 Header[] headers, Throwable throwable) {
		onHelperFail(context, callback, statusCode, headers, throwable, (String) null);
	}

	private static void onHelperFail(Context context, GeneralCallback callback, int statusCode,
	                                 Header[] headers, Throwable throwable,
	                                 JSONObject errorResponse) {
		onHelperFail(context, callback, statusCode, headers, throwable, errorResponse.toString());
	}

	private static void onHelperFail(Context context, GeneralCallback callback, int statusCode,
	                                 Header[] headers, Throwable throwable,
	                                 JSONArray errorResponse) {
		onHelperFail(context, callback, statusCode, headers, throwable, errorResponse.toString());
	}

	private static void onHelperFail(Context context, GeneralCallback callback, int statusCode,
	                                 Header[] headers, Throwable throwable, String errorMessage) {
		if (callback != null) {
			if (statusCode == 0) {
				callback.onFail(context.getString(R.string.timeout_message));
			} else {
				callback.onFail(context.getString(R.string.something_error) + "：" +
						statusCode + (errorMessage == null ? "" : " " + errorMessage));
			}
		}
	}

	public static void login(final Context context, String user, String pwd,
	                         final GeneralCallback callback) {
		// Basic Authorization
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pwd);
		mClient.setAuthenticationPreemptive(true);
		mClient.setCredentials(new AuthScope(SERVER_HOST, SERVER_PORT, AuthScope.ANY_REALM),
				credentials);

		mClient.get(context, LOGIN_URL, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				if (response.has("auth_token")) {
					if (callback != null) {
						callback.onSuccess();
					}
				} else {
					onHelperFail(context, callback, statusCode, headers);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void logout(final Context context, final GeneralCallback callback) {
		mClient.post(LOGOUT_URL, new TextHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
			                      Throwable throwable) {
				onHelperFail(context, callback, statusCode, headers, throwable, responseString);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if (statusCode == 200) {
					if (callback != null) {
						callback.onSuccess();
					}
				} else {
					onHelperFail(context, callback, statusCode, headers, null, responseString);
				}
			}
		});
	}

	public static void getServerStatus(final Context context, final ServerStatusCallback callback) {
		mClient.get(SERVER_STATUS_URL, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					ServerStatusModel model = new ServerStatusModel();
					JSONArray jsonArray = response.getJSONArray("status");
					for (int i = 0; i < jsonArray.length(); i++) {
						if (jsonArray.getJSONObject(i).getString("service").equals("ap")) {
							model.ap_status = jsonArray.getJSONObject(i).getInt("status");
						} else if (jsonArray.getJSONObject(i).getString("service")
								.equals("leave")) {
							model.leave_status = jsonArray.getJSONObject(i).getInt("status");
						} else if (jsonArray.getJSONObject(i).getString("service").equals("bus")) {
							model.bus_status = jsonArray.getJSONObject(i).getInt("status");
						}
					}
					if (callback != null) {
						callback.onSuccess(model);
					}
				} catch (JSONException e) {
					onHelperFail(context, callback, e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void getAppVersion(final Context context, final GeneralCallback callback) {
		mClient.get(APP_VERSION_URL, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					if (callback != null) {
						callback.onSuccess(response.getJSONObject("version").getString("version"));
					}
				} catch (JSONException e) {
					onHelperFail(context, callback, e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void getSemester(final Context context, final SemesterCallback callback) {
		mClient.get(SEMESTER_URL, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					List<SemesterModel> modelList = new ArrayList<>();
					JSONArray jsonArray = response.getJSONArray("semester");
					for (int i = 0; i < jsonArray.length(); i++) {
						SemesterModel model = new SemesterModel();
						model.selected = jsonArray.getJSONObject(i).getInt("selected") != 0;
						model.text = jsonArray.getJSONObject(i).getString("text");
						model.value = jsonArray.getJSONObject(i).getString("value");
						modelList.add(model);
					}
					SemesterModel selectedModel = new SemesterModel();
					selectedModel.selected =
							response.getJSONObject("default").getInt("selected") != 0;
					selectedModel.text = response.getJSONObject("default").getString("text");
					selectedModel.value = response.getJSONObject("default").getString("value");

					if (callback != null) {
						callback.onSuccess(modelList, selectedModel);
					}
				} catch (JSONException e) {
					onHelperFail(context, callback, e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void getAP_Query(final Context context, String fncid, String arg01, String arg02,
	                               String arg03, String arg04, final GeneralCallback callback) {
		RequestParams params = new RequestParams();
		params.put("fncid", fncid);
		params.put("arg01", arg01);
		params.put("arg02", arg02);
		params.put("arg03", arg03);
		params.put("arg04", arg04);
		mClient.post(AP_QUERY_URL, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				// TODO Wait for check this API response
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void userInfo(final Context context, final UserInfoCallback callback) {
		mClient.get(USER_INFO_URL, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					UserInfoModel model = new UserInfoModel();
					model.department = response.getString("model");
					model.education_system = response.getString("education_system");
					model.student_class = response.getString("class");
					model.student_id = response.getString("student_id");
					model.student_name_cht = response.getString("student_name_cht");
					model.student_name_eng = response.getString("student_name_eng");
					if (callback != null) {
						callback.onSuccess(model);
					}
				} catch (JSONException e) {
					onHelperFail(context, callback, e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void userPicture(final Context context, final GeneralCallback callback) {
		mClient.get(USER_PIC_URL, new TextHttpResponseHandler() {
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
			                      Throwable throwable) {
				onHelperFail(context, callback, statusCode, headers, throwable, responseString);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if (statusCode == 200) {
					if (callback != null) {
						callback.onSuccess(responseString);
					}
				} else {
					onHelperFail(context, callback, statusCode, headers, null, responseString);
				}
			}
		});
	}

	public static void getLeaveQuery(final Context context, String arg01, String arg02,
	                                 final GeneralCallback callback) {
		RequestParams params = new RequestParams();
		params.put("arg01", arg01);
		params.put("arg02", arg02);
		mClient.post(LEAVE_QUERY_URL, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				super.onSuccess(statusCode, headers, response);
				// TODO Wait for check this API response
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONArray errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void submitLeave(final Context context, String start_date, String end_date,
	                               int reason_id, String reason_text, String section,
	                               final GeneralCallback callback) {
		RequestParams params = new RequestParams();
		params.put("start_date", start_date);
		params.put("end_date", end_date);
		params.put("reason_id", reason_id);
		params.put("reason_text", reason_text);
		params.put("section", section);
		mClient.post(LEAVE_SUBMIT_URL, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				// TODO Wait for API
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void getBusQuery(final Context context, String date, final BusCallback callback) {
		RequestParams params = new RequestParams();
		if (date != null) {
			params.put("date", date);
		}
		mClient.get(BUS_QUERY_URL, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					List<BusModel> modelList = new ArrayList<>();
					JSONArray jsonArray = response.getJSONArray("timetable");
					for (int i = 0; i < jsonArray.length(); i++) {
						BusModel model = new BusModel();
						model.isReserve = jsonArray.getJSONObject(i).getInt("isReserve") != -1;
						model.EndEnrollDateTime =
								jsonArray.getJSONObject(i).getString("EndEnrollDateTime");
						model.runDateTime = jsonArray.getJSONObject(i).getString("runDateTime");
						model.endStation = jsonArray.getJSONObject(i).getString("endStation");
						model.limitCount = jsonArray.getJSONObject(i).getString("limitCount");
						model.reserveCount = jsonArray.getJSONObject(i).getString("reserveCount");
						model.Time = jsonArray.getJSONObject(i).getString("Time");
						model.busId = jsonArray.getJSONObject(i).getString("busId");
						modelList.add(model);
					}
					if (callback != null) {
						callback.onSuccess(modelList);
					}
				} catch (JSONException e) {
					onHelperFail(context, callback, e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void reserveBus(final Context context, final GeneralCallback callback) {
		mClient.get(BUS_RESERVE_URL, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				// TODO Wait for check this API response
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void bookingBus(final Context context, String busId, String action,
	                              final GeneralCallback callback) {
		RequestParams params = new RequestParams();
		params.put("busId", busId);
		params.put("action", action);
		mClient.post(BUS_BOOKING_URL, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				// TODO Wait for check this API response
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void getNotification(final Context context, int page,
	                                   final GeneralCallback callback) {
		String url = String.format(NOTIFICATION_URL, page);
		mClient.get(url, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				super.onSuccess(statusCode, headers, response);
				// TODO Wait for check this API response
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONArray errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void getNews(final Context context) {
		mClient.get(NEWS_URL, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				super.onSuccess(statusCode, headers, response);
				// TODO wait for API update
				try {
					Memory.setString(context, Constant.PREF_NEWS_TITLE, response.getString(2));
					Memory.setString(context, Constant.PREF_NEWS_CONTENT, response.getString(3));
					Memory.setString(context, Constant.PREF_NEWS_URL, response.getString(4));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void getNewsStatus(final Context context, final GeneralCallback callback) {
		mClient.get(NEWS_STATUS_URL, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				super.onSuccess(statusCode, headers, response);
				// TODO Wait for check this API response
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONArray errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
			}
		});
	}
}
