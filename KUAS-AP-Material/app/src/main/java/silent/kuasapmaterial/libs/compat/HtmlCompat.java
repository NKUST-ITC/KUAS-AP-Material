package silent.kuasapmaterial.libs.compat;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

/**
 * Legacy Html support for both N & pre-N
 */
public class HtmlCompat {

	@TargetApi(Build.VERSION_CODES.N)
	public static Spanned fromHtml(String source) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
		} else {
			return Html.fromHtml(source);
		}
	}
}
