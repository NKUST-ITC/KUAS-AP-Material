package silent.kuasapmaterial.libs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
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
}
