package silent.kuasapmaterial.libs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

public class Memory {
	private static SharedPreferences appSharedPrefs;
	private static Editor prefsEditor;

	@SuppressLint("CommitPrefEdits")
	public static void init(Context context) {
		appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefsEditor = appSharedPrefs.edit();
	}

	public static int getInt(Context context, String key, int defValue) {
		init(context);

		return appSharedPrefs.getInt(key, defValue);
	}

	public static void setInt(Context context, String key, int value) {
		init(context);

		prefsEditor.putInt(key, value);
		prefsEditor.commit();
	}

	public static long getLong(Context context, String key, long defValue) {
		init(context);

		return appSharedPrefs.getLong(key, defValue);
	}

	public static void setLong(Context context, String key, long value) {
		init(context);

		prefsEditor.putLong(key, value);
		prefsEditor.commit();
	}

	public static float getFloat(Context context, String key, float defValue) {
		init(context);

		return appSharedPrefs.getFloat(key, defValue);
	}

	public static void setFloat(Context context, String key, float value) {
		init(context);

		prefsEditor.putFloat(key, value);
		prefsEditor.commit();
	}

	public static String getString(Context context, String key, String defValue) {
		init(context);

		return appSharedPrefs.getString(key, defValue);
	}

	public static void setString(Context context, String key, String data) {
		init(context);

		prefsEditor.putString(key, data);
		prefsEditor.commit();
	}

	public static boolean getBoolean(Context context, String key, boolean defValue) {
		init(context);

		return appSharedPrefs.getBoolean(key, defValue);
	}

	public static void setBoolean(Context context, String key, boolean data) {
		init(context);

		prefsEditor.putBoolean(key, data);
		prefsEditor.commit();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Object getObject(Context context, String key, Class cls) {
		init(context);

		String json = appSharedPrefs.getString(key, null);

		return new Gson().fromJson(json, cls);
	}

	public static void setObject(Context context, String key, Object data) {
		init(context);

		String json = new Gson().toJson(data);
		prefsEditor.putString(key, json);
		prefsEditor.commit();
	}
}