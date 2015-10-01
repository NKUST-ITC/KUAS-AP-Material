package silent.kuasapmaterial.libs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

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
		final NotificationManager mNotificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		final NotificationCompat.Builder builder =
				new NotificationCompat.Builder(context).setContentTitle(title)
						.setStyle(new NotificationCompat.BigTextStyle().bigText(content))
						.setColor(context.getResources().getColor(R.color.main_theme))
						.setContentText(content).setAutoCancel(true)
						.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0))
						.setSmallIcon(R.mipmap.ic_launcher);

		builder.setVibrate(vibrationPattern);
		builder.setLights(Color.GREEN, 800, 800);
		builder.setDefaults(Notification.DEFAULT_SOUND);

		mNotificationManager.notify(id, builder.build());
	}
}
