package silent.kuasapmaterial;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;

import com.kuas.ap.donate.R;

import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Memory;
import silent.kuasapmaterial.libs.NotificationHelper;

public class CourseAlarmService extends Service {

	private PowerManager.WakeLock mCpuWakeLock;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		acquireCpuWakelock();

		// If user doesn't select "course" in settings, then don't show the notification.
		if (!Memory.getBoolean(this, Constant.PREF_COURSE_NOTIFY, false)) {
			stopService();
			if (intent == null) {
				return START_STICKY;
			} else {
				return super.onStartCommand(intent, flags, startId);
			}
		}

		if (intent == null) {
			stopService();
			return START_STICKY;
		}

		Bundle bundle = intent.getExtras();
		if (bundle == null) {
			stopService();
			return START_STICKY;
		}

		String title = bundle.getString("title", "");
		String room = bundle.getString("room", "");
		String time = bundle.getString("time", "");
		String content = getString(R.string.course_notify_content, time,
				room.length() == 0 ? getString(R.string.course_notify_unknown) : room);

		NotificationHelper
				.createNotification(this, title, content, Constant.NOTIFICATION_COURSE_ID);
		stopService();

		return super.onStartCommand(intent, flags, startId);
	}

	private void acquireCpuWakelock() {
		if (mCpuWakeLock != null) {
			return;
		}

		PowerManager powerManager = (PowerManager) getSystemService(Service.POWER_SERVICE);
		mCpuWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constant.TAG);
		mCpuWakeLock.acquire();
	}

	private void releaseCpuWakelock() {
		if (mCpuWakeLock != null) {
			mCpuWakeLock.release();
			mCpuWakeLock = null;
		}
	}

	private void stopService() {
		releaseCpuWakelock();
	}
}
