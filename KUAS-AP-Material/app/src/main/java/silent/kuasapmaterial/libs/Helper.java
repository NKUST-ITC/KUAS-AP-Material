package silent.kuasapmaterial.libs;

import android.content.Context;

import com.kuas.ap.R;
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
import java.util.Arrays;
import java.util.List;

import silent.kuasapmaterial.callback.BusCallback;
import silent.kuasapmaterial.callback.BusReservationsCallback;
import silent.kuasapmaterial.callback.CourseCallback;
import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.callback.LeaveCallback;
import silent.kuasapmaterial.callback.NotificationCallback;
import silent.kuasapmaterial.callback.ScoreCallback;
import silent.kuasapmaterial.callback.SemesterCallback;
import silent.kuasapmaterial.callback.ServerStatusCallback;
import silent.kuasapmaterial.callback.UserInfoCallback;
import silent.kuasapmaterial.models.BusModel;
import silent.kuasapmaterial.models.CourseModel;
import silent.kuasapmaterial.models.LeaveModel;
import silent.kuasapmaterial.models.LeaveSectionsModel;
import silent.kuasapmaterial.models.NotificationModel;
import silent.kuasapmaterial.models.ScoreDetailModel;
import silent.kuasapmaterial.models.ScoreModel;
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
		client.setTimeout(7 * 1000);
		client.getHttpClient().getParams()
				.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		return client;
	}

	public static final String SERVER_HOST = "kuas.grd.idv.tw";
	public static final int SERVER_PORT = 14769;
	public static final String BASE_URL = "https://" + SERVER_HOST + ":" + SERVER_PORT;

	public static final String SERVER_STATUS_URL = BASE_URL + "/latest/servers/status";
	public static final String APP_VERSION_URL = BASE_URL + "/latest/versions/android";
	public static final String LOGIN_URL = BASE_URL + "/latest/token";
	public static final String SEMESTER_URL = BASE_URL + "/latest/ap/semester";
	public static final String COURSE_TIMETABLE_URL =
			BASE_URL + "/latest/ap/users/coursetables/%s/%s";
	public static final String SCORE_TIMETABLE_URL = BASE_URL + "/latest/ap/users/scores/%s/%s";
	public static final String USER_INFO_URL = BASE_URL + "/latest/ap/users/info";
	public static final String USER_PIC_URL = BASE_URL + "/latest/ap/users/picture";
	public static final String LEAVE_TABLE_URL = BASE_URL + "/latest/leaves/%s/%s";
	public static final String LEAVE_SUBMIT_URL = BASE_URL + "/leave/submit";
	public static final String BUS_TIMETABLE_URL = BASE_URL + "/latest/bus/timetables";
	public static final String BUS_RESERVATIONS_URL = BASE_URL + "/latest/bus/reservations";
	public static final String BUS_BOOKING_URL = BASE_URL + "/latest/bus/reservations/%s";
	public static final String NOTIFICATION_URL = BASE_URL + "/latest/notifications/%s";
	public static final String NEWS_URL = BASE_URL + "/news";

	private static void onHelperTimeOut(GeneralCallback callback) {
		if (callback != null) {
			callback.onTimeOut();
		}
	}

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
		onHelperFail(context, callback, statusCode, headers, throwable,
				errorResponse == null ? null : errorResponse.toString());
	}

	private static void onHelperFail(Context context, GeneralCallback callback, int statusCode,
	                                 Header[] headers, Throwable throwable,
	                                 JSONArray errorResponse) {
		onHelperFail(context, callback, statusCode, headers, throwable,
				errorResponse == null ? null : errorResponse.toString());
	}

	private static void onHelperFail(Context context, GeneralCallback callback, int statusCode,
	                                 Header[] headers, Throwable throwable, String errorMessage) {
		if (callback != null) {
			if (errorMessage != null && errorMessage.toLowerCase().contains("token expired")) {
				callback.onTokenExpired();
			} else if (statusCode == 0) {
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
				if (statusCode == 200 && response != null && response.has("auth_token")) {
					if (callback != null) {
						callback.onSuccess();
					}
				} else {
					onHelperFail(context, callback, statusCode, headers);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
			                      Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				onHelperFail(context, callback, statusCode, headers, throwable, responseString);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers, throwable, errorResponse);
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

	public static void getCourseTimeTable(final Context context, String year, String semester,
	                                      final CourseCallback callback) {
		final List<String> weekdays = new ArrayList<>(
				Arrays.asList(context.getResources().getStringArray(R.array.course_weekdays)));
		final List<String> sections = new ArrayList<>(
				Arrays.asList(context.getResources().getStringArray(R.array.course_sections)));

		String url = String.format(COURSE_TIMETABLE_URL, year, semester);
		mClient.get(url, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					List<List<CourseModel>> modelList = new ArrayList<>();
					JSONObject coursetables = response.getJSONObject("coursetables");
					if (!coursetables.keys().hasNext()) {
						if (callback != null) {
							callback.onSuccess(modelList);
						}
						return;
					}
					for (int i = 0; i < weekdays.size(); i++) {
						if (coursetables.has(weekdays.get(i))) {
							List<CourseModel> tmpList = new ArrayList<>(
									Arrays.asList(new CourseModel[sections.size()]));
							JSONArray jsonArray = coursetables.getJSONArray(weekdays.get(i));
							for (int j = 0; j < jsonArray.length(); j++) {
								CourseModel model = new CourseModel();
								JSONObject jsonObject = jsonArray.getJSONObject(j);
								JSONObject dateObject = jsonObject.getJSONObject("date");
								JSONObject locationObject = jsonObject.getJSONObject("location");
								JSONArray instructorArray = jsonObject.getJSONArray("instructors");
								model.instructors = new ArrayList<>();
								for (int k = 0; k < instructorArray.length(); k++) {
									model.instructors.add(instructorArray.getString(k));
								}
								model.title = jsonObject.getString("title");
								model.start_time = dateObject.getString("start_time");
								model.end_time = dateObject.getString("end_time");
								model.weekday = dateObject.getString("weekday");
								model.section = dateObject.getString("section");
								model.building = locationObject.getString("building");
								model.room = locationObject.getString("room");
								tmpList.set(sections.indexOf(model.section), model);
							}
							modelList.add(i, tmpList);
						} else {
							modelList.add(i, null);
						}
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

	public static void getScoreTimeTable(final Context context, String year, String semester,
	                                     final ScoreCallback callback) {
		String url = String.format(SCORE_TIMETABLE_URL, year, semester);
		mClient.get(url, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					List<ScoreModel> modelList = new ArrayList<>();
					ScoreDetailModel scoreDetailModel = new ScoreDetailModel();
					JSONObject scores = response.getJSONObject("scores");
					if (!scores.keys().hasNext()) {
						if (callback != null) {
							callback.onSuccess(modelList, scoreDetailModel);
						}
						return;
					}
					JSONArray scoreList = scores.getJSONArray("scores");
					JSONObject detail = scores.getJSONObject("detail");
					for (int i = 0; i < scoreList.length(); i++) {
						JSONObject jsonObject = scoreList.getJSONObject(i);
						ScoreModel scoreModel = new ScoreModel();
						scoreModel.middle_score = jsonObject.getString("middle_score");
						scoreModel.final_score = jsonObject.getString("final_score");
						scoreModel.units = jsonObject.getString("units");
						scoreModel.remark = jsonObject.getString("remark");
						scoreModel.at = jsonObject.getString("at");
						scoreModel.hours = jsonObject.getString("hours");
						scoreModel.title = jsonObject.getString("title");
						scoreModel.required = jsonObject.getString("required");
						modelList.add(scoreModel);
					}
					scoreDetailModel.average = detail.getDouble("average");
					scoreDetailModel.class_percentage = detail.getDouble("class_percentage");
					scoreDetailModel.class_rank = detail.getString("class_rank");
					scoreDetailModel.conduct = detail.getDouble("conduct");
					if (callback != null) {
						callback.onSuccess(modelList, scoreDetailModel);
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

	public static void getUserInfo(final Context context, final UserInfoCallback callback) {
		mClient.get(USER_INFO_URL, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					UserInfoModel model = new UserInfoModel();
					model.department = response.getString("department");
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

	public static void getUserPicture(final Context context, final GeneralCallback callback) {
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

	public static void getLeaveTable(final Context context, String year, String semester,
	                                 final LeaveCallback callback) {
		String url = String.format(LEAVE_TABLE_URL, year, semester);
		mClient.get(url, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray leaves = response.getJSONArray("leaves");
					List<LeaveModel> modelList = new ArrayList<>();
					for (int i = 0; i < leaves.length(); i++) {
						JSONObject jsonObject = leaves.getJSONObject(i);
						LeaveModel model = new LeaveModel();
						model.leave_sections = new ArrayList<>();
						JSONArray jsonArray = jsonObject.getJSONArray("leave_sections");
						for (int j = 0; j < jsonArray.length(); j++) {
							LeaveSectionsModel leaveSectionsModel = new LeaveSectionsModel();
							JSONObject sectionObject = jsonArray.getJSONObject(j);
							leaveSectionsModel.reason = sectionObject.getString("reason");
							leaveSectionsModel.section = sectionObject.getString("section");
							model.leave_sections.add(leaveSectionsModel);
						}
						model.date = jsonObject.getString("date");
						model.instructors_comment = jsonObject.getString("instructors_comment");
						model.leave_sheet_id = jsonObject.getString("leave_sheet_id");
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

	public static void getBusTimeTable(final Context context, String date,
	                                   final BusCallback callback) {
		RequestParams params = new RequestParams();
		if (date != null) {
			params.put("date", date);
		}
		mClient.get(BUS_TIMETABLE_URL, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					List<BusModel> jiangongList = new ArrayList<>();
					List<BusModel> yanchaoList = new ArrayList<>();
					JSONArray jsonArray = response.getJSONArray("timetable");
					for (int i = 0; i < jsonArray.length(); i++) {
						BusModel model = new BusModel();
						model.isReserve = jsonArray.getJSONObject(i).getInt("isReserve") != 0;
						model.EndEnrollDateTime =
								jsonArray.getJSONObject(i).getString("EndEnrollDateTime");
						model.runDateTime = jsonArray.getJSONObject(i).getString("runDateTime");
						model.endStation = jsonArray.getJSONObject(i).getString("endStation");
						model.limitCount = jsonArray.getJSONObject(i).getString("limitCount");
						model.reserveCount = jsonArray.getJSONObject(i).getString("reserveCount");
						model.Time = jsonArray.getJSONObject(i).getString("Time");
						model.busId = jsonArray.getJSONObject(i).getString("busId");
						model.cancelKey = jsonArray.getJSONObject(i).getString("cancelKey");
						if (model.endStation.equals("建工")) {
							yanchaoList.add(model);
						} else {
							jiangongList.add(model);
						}
					}
					if (callback != null) {
						callback.onSuccess(jiangongList, yanchaoList);
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

	public static void getBusReservations(final Context context,
	                                      final BusReservationsCallback callback) {
		mClient.get(BUS_RESERVATIONS_URL, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray jsonArray = response.getJSONArray("reservation");
					List<BusModel> modelList = new ArrayList<>();
					for (int i = 0; i < jsonArray.length(); i++) {
						BusModel model = new BusModel();
						model.EndEnrollDateTime = jsonArray.getJSONObject(i).getString("endTime");
						model.endStation = jsonArray.getJSONObject(i).getString("end");
						model.Time = jsonArray.getJSONObject(i).getString("time");
						model.cancelKey = jsonArray.getJSONObject(i).getString("cancelKey");
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

	public static void bookingBus(final Context context, String busId,
	                              final GeneralCallback callback) {
		String url = String.format(BUS_BOOKING_URL, busId);
		mClient.put(url, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				if (response == null) {
					onHelperFail(context, callback, statusCode, headers);
					return;
				}
				try {
					if (!response.getBoolean("success")) {
						if (callback != null) {
							callback.onFail(response.getString("message"));
						}
					} else if (response.has("success") && response.getBoolean("success")) {
						if (callback != null) {
							callback.onSuccess();
						}
					} else {
						onHelperFail(context, callback, statusCode, headers, null, response);
					}
				} catch (JSONException e) {
					onHelperFail(context, callback, e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				try {
					if (errorResponse != null && errorResponse.has("message")) {
						if (callback != null) {
							callback.onFail(errorResponse.getString("message"));
						}
					} else {
						onHelperFail(context, callback, statusCode, headers, throwable,
								errorResponse);
					}
				} catch (JSONException e) {
					onHelperFail(context, callback, e);
				}
			}
		});
	}

	public static void cancelBookingBus(final Context context, String cancelKey,
	                                    final GeneralCallback callback) {
		String url = String.format(BUS_BOOKING_URL, cancelKey);
		mClient.delete(url, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				if (response == null) {
					onHelperFail(context, callback, statusCode, headers);
					return;
				}
				try {
					if (!response.getBoolean("success")) {
						if (callback != null) {
							callback.onFail(response.getString("message"));
						}
					} else if (response.has("success") && response.getBoolean("success")) {
						if (callback != null) {
							callback.onSuccess();
						}
					} else {
						onHelperFail(context, callback, statusCode, headers, null, response);
					}
				} catch (JSONException e) {
					onHelperFail(context, callback, e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				try {
					if (errorResponse != null && errorResponse.has("message")) {
						if (callback != null) {
							callback.onFail(errorResponse.getString("message"));
						}
					} else {
						onHelperFail(context, callback, statusCode, headers, throwable,
								errorResponse);
					}
				} catch (JSONException e) {
					onHelperFail(context, callback, e);
				}
			}
		});
	}

	public static void getNotification(final Context context, int page,
	                                   final NotificationCallback callback) {
		String url = String.format(NOTIFICATION_URL, page);
		mClient.get(url, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					List<NotificationModel> modelList = new ArrayList<>();
					JSONArray jsonArray = response.getJSONArray("notification");
					for (int i = 0; i < jsonArray.length(); i++) {
						NotificationModel model = new NotificationModel();
						model.link = jsonArray.getJSONObject(i).getString("link");
						JSONObject infoObject = jsonArray.getJSONObject(i).getJSONObject("info");
						model.date = infoObject.getString("date");
						model.content = infoObject.getString("title");
						model.author = infoObject.getString("department");
						model.id = infoObject.getString("id");
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
			public void onFailure(int statusCode, Header[] headers, String responseString,
			                      Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				onHelperFail(context, callback, statusCode, headers, throwable);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONObject errorResponse) {
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
}
