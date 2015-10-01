package silent.kuasapmaterial.libs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import silent.kuasapmaterial.BusAlarmService;
import silent.kuasapmaterial.CourseAlarmService;
import silent.kuasapmaterial.models.BusModel;
import silent.kuasapmaterial.models.CourseModel;

public class AlarmHelper {

	public static void setBusNotification(Context context, List<BusModel> busModelList) {
		if (busModelList == null || busModelList.size() == 0) {
			return;
		}

		// Must cancel bus alarm if user cancel on web
		List<BusModel> savedBusModelList = Utils.loadBusNotify(context);
		if (savedBusModelList != null && savedBusModelList.size() != 0) {
			for (int i = 0; i < savedBusModelList.size(); i++) {
				if (!busModelList.contains(savedBusModelList.get(i))) {
					BusModel model = savedBusModelList.get(i);
					cancelBusAlarm(context, model.endStation, model.runDateTime,
							Integer.parseInt(model.cancelKey));
				}
			}
		}

		Utils.saveBusNotify(context, busModelList);
		for (int i = 0; i < busModelList.size(); i++) {
			BusModel model = busModelList.get(i);
			setBusAlarm(context, model.endStation, model.runDateTime,
					Integer.parseInt(model.cancelKey));
		}
	}

	public static void setBusNotification(Context context) {
		List<BusModel> busModelList = Utils.loadBusNotify(context);
		if (busModelList == null || busModelList.size() == 0) {
			return;
		}
		for (int i = 0; i < busModelList.size(); i++) {
			BusModel model = busModelList.get(i);
			setBusAlarm(context, model.endStation, model.runDateTime,
					Integer.parseInt(model.cancelKey));
		}
	}

	public static void setCourseNotification(Context context,
	                                         List<List<CourseModel>> courseModelList) {
		if (courseModelList == null) {
			return;
		}

		List<String> keyList = new ArrayList<>();
		List<CourseModel> saveModelList = new ArrayList<>();
		for (int i = 0; i < courseModelList.size(); i++) {
			if (courseModelList.get(i) != null) {
				for (int j = 0; j < courseModelList.get(i).size(); j++) {
					if (courseModelList.get(i).get(j) != null) {
						if (keyList.contains(courseModelList.get(i).get(j).title + i)) {
							continue;
						} else {
							keyList.add(courseModelList.get(i).get(j).title + i);
						}

						CourseModel courseModel = courseModelList.get(i).get(j);
						courseModel.dayOfWeek = i == 6 ? 1 : (i + 2);
						courseModel.notifyKey = j * 10 + i;
						saveModelList.add(courseModel);

						setCourseAlarm(context, courseModel.room.trim(), courseModel.title,
								courseModel.start_time, courseModel.dayOfWeek,
								courseModel.notifyKey);
					}
				}
			}
		}

		// Must cancel course alarm if user cancel on web
		List<CourseModel> savedCourseModelList = Utils.loadCourseNotify(context);
		if (savedCourseModelList != null && savedCourseModelList.size() != 0) {
			for (int i = 0; i < savedCourseModelList.size(); i++) {
				if (!saveModelList.contains(savedCourseModelList.get(i))) {
					CourseModel courseModel = savedCourseModelList.get(i);
					cancelCourseAlarm(context, courseModel.room.trim(), courseModel.title,
							courseModel.start_time, courseModel.notifyKey);
				}
			}
		}

		Utils.saveCourseNotify(context, saveModelList);
	}

	public static void setCourseNotification(Context context) {
		List<CourseModel> courseModelList = Utils.loadCourseNotify(context);
		if (courseModelList == null) {
			return;
		}
		for (int i = 0; i < courseModelList.size(); i++) {
			setCourseAlarm(context, courseModelList.get(i).room.trim(),
					courseModelList.get(i).title, courseModelList.get(i).start_time,
					courseModelList.get(i).dayOfWeek, courseModelList.get(i).notifyKey);
		}
	}

	public static void cancelBusAlarm(Context context, String endStation, String time, int id) {
		Intent intent = new Intent(context, BusAlarmService.class);

		Bundle bundle = new Bundle();
		bundle.putString("endStation", endStation);
		bundle.putString("Time", time);
		intent.putExtras(bundle);

		PendingIntent pendingIntent =
				PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarm = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
		alarm.cancel(pendingIntent);
	}

	public static void setBusAlarm(Context context, String endStation, String time, int id) {
		Intent intent = new Intent(context, BusAlarmService.class);

		Bundle bundle = new Bundle();
		bundle.putString("endStation", endStation);
		bundle.putString("Time", time);
		intent.putExtras(bundle);

		Calendar calendar = Calendar.getInstance();
		String _date = time.split(" ")[0];
		String _time = time.split(" ")[1];
		calendar.set(Integer.parseInt(_date.split("-")[0]),
				Integer.parseInt(_date.split("-")[1]) - 1, Integer.parseInt(_date.split("-")[2]),
				Integer.parseInt(_time.split(":")[0]), Integer.parseInt(_time.split(":")[1]));
		calendar.add(Calendar.MINUTE, -30);
		PendingIntent pendingIntent =
				PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarm = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
		alarm.cancel(pendingIntent);
		Date now = new Date(System.currentTimeMillis());
		if (calendar.getTime().after(now)) {
			alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
		}
	}

	public static void cancelCourseAlarm(Context context, String room, String title, String time,
	                                     int id) {
		Intent intent = new Intent(context, CourseAlarmService.class);

		Bundle bundle = new Bundle();
		bundle.putString("room", room);
		bundle.putString("title", title);
		bundle.putString("time", time);
		intent.putExtras(bundle);

		PendingIntent pendingIntent =
				PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
		alarm.cancel(pendingIntent);
	}

	public static void setCourseAlarm(Context context, String room, String title, String time,
	                                  int dayOfWeek, int id) {
		Intent intent = new Intent(context, CourseAlarmService.class);

		Bundle bundle = new Bundle();
		bundle.putString("room", room);
		bundle.putString("title", title);
		bundle.putString("time", time);
		intent.putExtras(bundle);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.split(":")[0]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(time.split(":")[1]));
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.MINUTE, -10);
		PendingIntent pendingIntent =
				PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Date now = new Date(System.currentTimeMillis());
		if (calendar.getTime().before(now)) {
			calendar.add(Calendar.DAY_OF_MONTH, 7);
		}

		AlarmManager alarm = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
		alarm.cancel(pendingIntent);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY * 7, pendingIntent);
	}
}
