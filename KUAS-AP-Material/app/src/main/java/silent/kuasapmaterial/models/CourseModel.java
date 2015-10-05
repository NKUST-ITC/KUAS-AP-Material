package silent.kuasapmaterial.models;

import android.support.annotation.NonNull;

import java.util.List;

public class CourseModel implements Comparable<CourseModel> {
	public List<String> instructors;
	public String title;
	public String building;
	public String room;
	public String start_time;
	public String end_time;
	public String weekday;
	public String section;
	public int dayOfWeek;
	public int notifyKey;

	@Override
	public boolean equals(Object o) {
		return o instanceof CourseModel && title.equals(((CourseModel) o).title) &&
				room.equals(((CourseModel) o).room) &&
				start_time.equals(((CourseModel) o).start_time) &&
				weekday.equals(((CourseModel) o).weekday) &&
				section.equals(((CourseModel) o).section);
	}

	@Override
	public int compareTo(@NonNull CourseModel other) {
		return Math.abs(title.compareTo(other.title)) + Math.abs(room.compareTo(other.room)) +
				Math.abs(start_time.compareTo(other.start_time)) +
				Math.abs(weekday.compareTo(other.weekday)) +
				Math.abs(section.compareTo(other.section));
	}
}