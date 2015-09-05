package silent.kuasapmaterial;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import silent.kuasapmaterial.libs.PinnedSectionListView;

public class EventActivity extends AppCompatActivity {

	PinnedSectionListView mListView;
	List<String> mScheduleList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);

		findViews();
		setUpViews();
	}

	private void findViews() {
		mListView = (PinnedSectionListView) findViewById(R.id.listView);
	}

	private void setUpViews() {
		mListView.setDividerHeight(0);

		mScheduleList = new ArrayList<>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(getAssets().open("schedule.txt"), "UTF-8"));

			String mLine = reader.readLine();
			while (mLine != null) {
				mScheduleList.add(mLine);
				mLine = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		ScheduleAdapter adapter = new ScheduleAdapter(this);
		mListView.setAdapter(adapter);
	}

	public class ScheduleAdapter extends BaseAdapter
			implements PinnedSectionListView.PinnedSectionListAdapter {
		private LayoutInflater inflater;

		private static final int TYPE_WEEK = 0;
		private static final int TYPE_SCHEDULE = 1;

		public ScheduleAdapter(Context context) {
			this.inflater =
					(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mScheduleList.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public String getItem(int position) {
			return mScheduleList.get(position);
		}

		@Override
		public int getViewTypeCount() {
			// TYPE_SCHOOL and TYPE_TITLE
			return 2;
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {
			return viewType == TYPE_WEEK;
		}

		@Override
		public int getItemViewType(int position) {
			if (getItem(position).startsWith("*")) {
				return TYPE_SCHEDULE;
			}
			return TYPE_WEEK;
		}

		@Override
		public boolean isEnabled(int position) {
			return (getItemViewType(position) != TYPE_WEEK);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				switch (type) {
					case TYPE_WEEK:
						convertView =
								inflater.inflate(R.layout.list_schedule_header, parent, false);
						break;

					default:
						convertView = inflater.inflate(R.layout.list_schedule_item, parent, false);
				}
				holder.textView = (TextView) convertView.findViewById(R.id.textView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (type == TYPE_WEEK) {
				holder.textView.setText(mScheduleList.get(position));
			} else {
				holder.textView.setText(mScheduleList.get(position).substring(1));
			}
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}
}
