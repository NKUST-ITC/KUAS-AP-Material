package silent.kuasapmaterial.libs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.kuas.ap.donate.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import silent.kuasapmaterial.LoginActivity;
import silent.kuasapmaterial.callback.BusReservationsCallback;
import silent.kuasapmaterial.callback.CourseCallback;
import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.callback.SemesterCallback;
import silent.kuasapmaterial.models.BusModel;
import silent.kuasapmaterial.models.CourseModel;
import silent.kuasapmaterial.models.SemesterModel;

public class Utils {

	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	public static boolean isWide(Context context) {
		return context.getResources().getBoolean(R.bool.wide);
	}

	public static boolean isLand(Context context) {
		return context.getResources().getConfiguration().orientation ==
				Configuration.ORIENTATION_LANDSCAPE;
	}

	public static byte[] EncryptAES(byte[] iv, byte[] key, byte[] text) {
		try {
			AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
			SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
			Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec);

			return mCipher.doFinal(text);
		} catch (Exception ex) {
			return null;
		}
	}

	public static byte[] DecryptAES(byte[] iv, byte[] key, byte[] text) {
		try {
			AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
			SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
			Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec);

			return mCipher.doFinal(text);
		} catch (Exception ex) {
			return null;
		}
	}

	public static AlertDialog createLoadingDialog(Context context, int contentRes) {
		return createLoadingDialog(context, null, context.getText(contentRes).toString());
	}

	public static AlertDialog createLoadingDialog(Context context, int titleRes, int contentRes) {
		return createLoadingDialog(context, context.getText(titleRes).toString(),
				context.getText(contentRes).toString());
	}

	@SuppressLint("InflateParams")
	public static AlertDialog createLoadingDialog(Context context, String title, String content) {
		View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
		if (content != null) {
			((TextView) dialogView.findViewById(R.id.textView_content)).setText(content);
		}
		return new AlertDialog.Builder(context).setTitle(title).setView(dialogView)
				.setCancelable(false).create();
	}

	@SuppressLint("InflateParams")
	public static AlertDialog createTokenExpired(final Activity activity) {
		return new AlertDialog.Builder(activity).setTitle(R.string.token_expired_title)
				.setMessage(R.string.token_expired_content)
				.setPositiveButton(R.string.determine, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (activity.isFinishing()) {
							return;
						}
						activity.startActivity(new Intent(activity, LoginActivity.class));
						activity.finish();
					}
				}).setCancelable(false).create();
	}

	@SuppressLint("InflateParams")
	public static AlertDialog createForceUpdateDialog(final Activity activity) {
		return new AlertDialog.Builder(activity).setTitle(R.string.update_title)
				.setMessage(R.string.update_content)
				.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (activity.isFinishing()) {
							return;
						}
						activity.startActivity(new Intent(Intent.ACTION_VIEW,
								Uri.parse("market://details?id=" + activity.getPackageName())));
						activity.finish();
					}
				}).setCancelable(false).create();
	}

	@SuppressLint("InflateParams")
	public static AlertDialog createUpdateDialog(final Activity activity) {
		return new AlertDialog.Builder(activity).setTitle(R.string.update_title)
				.setMessage(R.string.update_content)
				.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (activity.isFinishing()) {
							return;
						}
						activity.startActivity(new Intent(Intent.ACTION_VIEW,
								Uri.parse("market://details?id=" + activity.getPackageName())));
					}
				}).setNegativeButton(R.string.skip, null).create();
	}

	public static int[] getSwipeRefreshColors(Context context) {
		return new int[]{ContextCompat.getColor(context, R.color.progress_red),
				ContextCompat.getColor(context, R.color.progress_blue),
				ContextCompat.getColor(context, R.color.progress_yellow),
				ContextCompat.getColor(context, R.color.progress_green)};
	}

	/**
	 * White colors can be transformed in to different colors.
	 *
	 * @param sourceBitmap The source Bitmap
	 * @param color        The different color
	 * @return The different color Bitmap
	 */
	public static Bitmap changeImageColor(Bitmap sourceBitmap, int color) {
		Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth() - 1,
				sourceBitmap.getHeight() - 1);
		Paint p = new Paint();
		ColorFilter filter = new LightingColorFilter(color, 1);
		p.setColorFilter(filter);

		Canvas canvas = new Canvas(resultBitmap);
		canvas.drawBitmap(resultBitmap, 0, 0, p);
		return resultBitmap;
	}

	public static Drawable covertBitmapToDrawable(Context context, Bitmap bitmap) {
		return new BitmapDrawable(context.getResources(), bitmap);
	}

	public static Bitmap convertDrawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap =
				Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
						Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static DisplayImageOptions.Builder getDefaultDisplayImageBuilder() {
		return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.displayer(new FadeInBitmapDisplayer(500));
	}

	public static DisplayImageOptions getDefaultDisplayImageOptions() {
		// big light loading + fade in
		return getDefaultDisplayImageBuilder().build();
	}

	public static DisplayImageOptions getHeadDisplayImageOptions(final Context context,
	                                                             final int cornerPixels) {
		// rounded head
		return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.showImageOnLoading(R.drawable.ic_account_circle_white_48dp)
				.preProcessor(new BitmapProcessor() {

					public Bitmap process(Bitmap src) {
						if (src.getWidth() == 0 || src.getHeight() == 0) {
							return BitmapFactory.decodeResource(context.getResources(),
									R.drawable.ic_account_circle_white_48dp);
						}

						Bitmap result;
						Matrix matrix = new Matrix();
						if (src.getWidth() >= src.getHeight()) {
							float scale = src.getHeight() / cornerPixels * 2f;
							matrix.setScale(scale, scale);
							result = Bitmap.createBitmap(src,
									src.getWidth() / 2 - src.getHeight() / 2, 0, src.getHeight(),
									src.getHeight(), matrix, false);

						} else {
							float scale = src.getWidth() / cornerPixels * 2f;
							matrix.setScale(scale, scale);
							result = Bitmap.createBitmap(src, 0,
									src.getHeight() / 2 - src.getWidth() / 2, src.getWidth(),
									src.getWidth(), matrix, false);
						}
						src.recycle();

						return result;
					}
				}).displayer(new RoundedBitmapDisplayer(cornerPixels)).build();
	}

	public static void hideSoftKeyboard(@NonNull Activity activity) {
		InputMethodManager inputManager =
				(InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (activity.getCurrentFocus() != null) {
			inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static Point getDisplayDimen(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public static int getDisplayHeight(Context context) {
		return getDisplayDimen(context).y;
	}

	public static int getDisplayWidth(Context context) {
		return getDisplayDimen(context).x;
	}

	public static PendingIntent createSharePendingIntent(Context context, String content) {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, content);
		sendIntent.setType("text/plain");
		return PendingIntent.getActivity(context, 0, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	/**
	 * Save Notify Data
	 */
	public static void saveCourseNotify(Context context, List<CourseModel> modelList) {
		Memory.setObject(context, Constant.PREF_COURSE_NOTIFY_DATA, modelList);
	}

	public static void saveBusNotify(Context context, List<BusModel> modelList) {
		Memory.setObject(context, Constant.PREF_BUS_NOTIFY_DATA, modelList);
	}

	/**
	 * Load Notify Data
	 */
	public static List<CourseModel> loadCourseNotify(Context context) {
		CourseModel[] courseModels = (CourseModel[]) Memory
				.getObject(context, Constant.PREF_COURSE_NOTIFY_DATA, CourseModel[].class);
		return courseModels == null ? null : new ArrayList<>(Arrays.asList(courseModels));
	}

	public static List<BusModel> loadBusNotify(Context context) {
		BusModel[] busModels = (BusModel[]) Memory
				.getObject(context, Constant.PREF_BUS_NOTIFY_DATA, BusModel[].class);
		return busModels == null ? null : new ArrayList<>(Arrays.asList(busModels));
	}

	/**
	 * Set Up Notify and Vibrate
	 */
	public static void setUpCourseNotify(final Context context, final GeneralCallback callback) {
		Helper.getSemester(context, new SemesterCallback() {
			@Override
			public void onSuccess(List<SemesterModel> modelList, SemesterModel selectedModel) {
				super.onSuccess(modelList, selectedModel);
				Helper.getCourseTimeTable(context, selectedModel.value.split(",")[0],
						selectedModel.value.split(",")[1], new CourseCallback() {
							@Override
							public void onSuccess(List<List<CourseModel>> modelList) {
								super.onSuccess(modelList);
								AlarmHelper.setCourseNotification(context, modelList);
								VibrateHelper.setCourseVibrate(context, modelList);
								callback.onSuccess();
							}

							@Override
							public void onTokenExpired() {
								super.onTokenExpired();
								callback.onTokenExpired();
							}

							@Override
							public void onFail(String errorMessage) {
								super.onFail(errorMessage);
								callback.onFail(errorMessage);
							}
						});
			}

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);
				callback.onFail(errorMessage);
			}
		});
	}

	public static void setUpBusNotify(final Context context, final GeneralCallback callback) {
		Helper.getBusReservations(context, new BusReservationsCallback() {
			@Override
			public void onSuccess(List<BusModel> modelList) {
				super.onSuccess(modelList);
				AlarmHelper.setBusNotification(context, modelList);
				callback.onSuccess();
			}

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);
				callback.onFail(errorMessage);
			}

			@Override
			public void onTokenExpired() {
				super.onTokenExpired();
				callback.onTokenExpired();
			}
		});
	}

	/**
	 * Save Vibrate Data
	 */
	public static void saveCourseVibrate(Context context, List<CourseModel> modelList) {
		Memory.setObject(context, Constant.PREF_COURSE_VIBRATE_DATA, modelList);
	}

	/**
	 * Load Vibrate Data
	 */
	public static List<CourseModel> loadCourseVibrate(Context context) {
		CourseModel[] courseModels = (CourseModel[]) Memory
				.getObject(context, Constant.PREF_COURSE_VIBRATE_DATA, CourseModel[].class);
		return courseModels == null ? null : new ArrayList<>(Arrays.asList(courseModels));
	}
}
