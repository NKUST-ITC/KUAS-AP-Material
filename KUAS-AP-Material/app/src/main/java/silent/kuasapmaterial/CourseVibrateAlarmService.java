package silent.kuasapmaterial;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;

import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Memory;

public class CourseVibrateAlarmService extends Service {

	private PowerManager.WakeLock mCpuWakeLock;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		acquireCpuWakelock();

		// If user doesn't select "course" in settings, then don't show the notification.
		if (!Memory.getBoolean(this, Constant.PREF_COURSE_VIBRATE, false)) {
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

		Boolean isVibrate = bundle.getBoolean("mode", false);

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
				!notificationManager.isNotificationPolicyAccessGranted()) {
			stopService();
			return START_STICKY;
		}
		if (isVibrate) {
			Memory.setInt(this, Constant.PREF_COURSE_VIBRATE_USER_SETTING,
					audioManager.getRingerMode());
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		} else {
			audioManager.setRingerMode(
					Memory.getInt(this, Constant.PREF_COURSE_VIBRATE_USER_SETTING,
							audioManager.getRingerMode()));
		}

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
