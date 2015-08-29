package silent.kuasapmaterial.libs;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.client.params.ClientPNames;
import org.json.JSONArray;
import org.json.JSONObject;

import silent.kuasapmaterial.R;
import silent.kuasapmaterial.callback.GeneralCallback;

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

	public static final String BASE_URL = "http://kuas.grd.idv.tw:14768/";
	public static final String BACKUP_URL = "http://api.grd.idv.tw:14768/";
	public static final String SERVER_STATUS_URL = BASE_URL + "status";
	public static final String APP_VERSION_URL = BASE_URL + "android_version";
	public static final String LOGIN_URL = BASE_URL + "ap/login";
	public static final String CHECK_LOGIN_URL = BASE_URL + "ap/is_login";

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

	public static void checkLogin(final Context context, final GeneralCallback callback) {
		mClient.get(CHECK_LOGIN_URL, new TextHttpResponseHandler() {
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
			                      Throwable throwable) {
				if (callback != null) {
					callback.onFail(context.getString(R.string.something_error));
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if (callback != null) {
					Log.d(Constant.TAG, responseString);
					if (responseString.equals("true")) {
						callback.onSuccess();
					} else {
						callback.onFail(context.getString(R.string.something_error));
					}
				}
			}
		});
	}

	public static void login(final Context context, String user, String pwd,
	                         final GeneralCallback callback) {
		PersistentCookieStore persistentCookieStore = new PersistentCookieStore(context);
		mClient.setCookieStore(persistentCookieStore);

		RequestParams params = new RequestParams();
		params.put("uid", user);
		params.put("pwd", pwd);
		mClient.post(LOGIN_URL, params, new TextHttpResponseHandler() {
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
			                      Throwable throwable) {
				if (callback != null) {
					callback.onFail(context.getString(R.string.something_error));
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				checkLogin(context, callback);
			}
		});
	}

	public static void getServerStatus(final Context context, final GeneralCallback callback) {
		mClient.get(SERVER_STATUS_URL, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				super.onSuccess(statusCode, headers, response);
				if (callback != null) {
					callback.onSuccess(response);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			                      JSONArray errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				onHelperFail(context, callback, statusCode, headers);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
			                      Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				onHelperFail(context, callback, statusCode, headers);
			}
		});
	}

	public static void getAppVersion(final Context context, final GeneralCallback callback) {
		mClient.get(APP_VERSION_URL, new TextHttpResponseHandler() {
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
			                      Throwable throwable) {
				onHelperFail(context, callback, statusCode, headers);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if (callback != null) {
					callback.onSuccess(responseString);
				}
			}
		});
	}
}
