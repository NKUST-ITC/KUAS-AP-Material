package silent.kuasapmaterial.libs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import silent.kuasapmaterial.R;

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

	public static int[] getSwipeRefreshColors(Context context) {
		Resources res = context.getResources();
		return new int[]{res.getColor(R.color.progress_red), res.getColor(R.color.progress_blue),
				res.getColor(R.color.progress_yellow), res.getColor(R.color.progress_green)};
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
}
