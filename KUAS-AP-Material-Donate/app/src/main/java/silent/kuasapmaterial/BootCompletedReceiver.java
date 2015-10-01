package silent.kuasapmaterial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import silent.kuasapmaterial.libs.AlarmHelper;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		AlarmHelper.setBusNotification(context);
		AlarmHelper.setCourseNotification(context);
	}
}
