package silent.kuasapmaterial.libs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import silent.kuasapmaterial.R;

public class Utils {

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
}
