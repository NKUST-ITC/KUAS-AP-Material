package silent.kuasapmaterial.libs;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.kuas.ap.R;

public class NotificationHelper {
	private static long[] vibrationPattern = {300, 200, 300, 200};

	/**
	 * Create a notification
	 *
	 * @param context The application context
	 * @param title   Notification title
	 * @param content Notification content.
	 */
	public static void createNotification(final Context context, final String title,
	                                      final String content, int id) {
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

		NotificationCompat.Builder builder =
				new NotificationCompat.Builder(context).setContentTitle(title)
						.setStyle(new NotificationCompat.BigTextStyle().bigText(content))
						.setColor(ContextCompat.getColor(context, R.color.main_theme))
						.extend(new NotificationCompat.WearableExtender()
								.setHintShowBackgroundOnly(true)).setContentText(content)
						.setAutoCancel(true)
						.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0))
						.setSmallIcon(R.mipmap.ic_launcher);

		builder.setVibrate(vibrationPattern);
		builder.setLights(Color.GREEN, 800, 800);
		builder.setDefaults(Notification.DEFAULT_SOUND);

		notificationManager.notify(id, builder.build());
	}
}
