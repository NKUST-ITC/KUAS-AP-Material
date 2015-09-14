package silent.kuasapmaterial.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.kuas.ap.donate.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import silent.kuasapmaterial.base.SilentFragment;
import silent.kuasapmaterial.libs.PinnedSectionListView;

public class ScheduleFragment extends SilentFragment implements AdapterView.OnItemClickListener {

	private View view;
	private PinnedSectionListView mListView;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	List<String> mList;
	Activity activity;

	private int mInitListPos = 0, mInitListOffset = 0;

	String mScheduleData =
			"[{\"events\": [\"(9/14) 日間部、進修推廣處開學\"], \"week\": \"第一週\"}, {\"events\": [\"(9/28) " +
					"補假一天(補 9/27 中秋節)\"], \"week\": \"第三週\"}, {\"events\": [\"(10/9) 補假一天(補 10/10" +
					" 國慶日)\"], \"week\": \"第四週\"}, {\"events\": [\"(10/25) 校運會\", \"(10/29) " +
					"校運會補假ㄧ天\", \"(10/30) 校慶放假一天\"], \"week\": \"第七週\"}, {\"events\": [\"" +
					"(11/9 ~ 11/14) 日間部、進修推廣處期中考試\"], \"week\": \"第九週\"}, {\"events\": [\"(1/1) " +
					"開國紀念日放假ㄧ天\"], \"week\": \"第十六週\"}, {\"events\": [\"(1/9 ~ 1/15) " +
					"日間部、進修推廣處期末考試\"], \"week\": \"第十八週\"}, {\"events\": [\"(1/30) 補行上班\"], " +
					"\"week\": \"第十週\"}, {\"events\": [\"(11/26) 燕巢 三合一選舉\", \"(11/27) 建工 " +
					"三合一選舉\"], \"week\": \"寒假\"}]";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		view = inflater.inflate(R.layout.fragment_messages, container, false);

		restoreArgs(savedInstanceState);
		findViews();
		setUpViews();

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		initGA("Messages Screen", activity);
	}

	private void restoreArgs(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mInitListPos = savedInstanceState.getInt("mInitListPos");
			mInitListOffset = savedInstanceState.getInt("mInitListOffset");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mListView != null) {
			outState.putInt("mInitListPos", mListView.getFirstVisiblePosition());
			View vNewTop = mListView.getChildAt(0);
			outState.putInt("mInitListOffset", (vNewTop == null) ? 0 : vNewTop.getTop());
		}
	}

	private void findViews() {
		mListView = (PinnedSectionListView) view.findViewById(R.id.listView);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
	}

	private void setUpViews() {
		// TODO wait for API
		mSwipeRefreshLayout.setEnabled(false);
		mListView.setDividerHeight(0);

		mList = new ArrayList<>();
		try {
			JSONArray jsonArray = new JSONArray(mScheduleData.trim());
			for (int i = 0; i < jsonArray.length(); i++) {
				mList.add(jsonArray.getJSONObject(i).getString("week"));
				JSONArray eventArray = jsonArray.getJSONObject(i).getJSONArray("events");
				for (int j = 0; j < eventArray.length(); j++) {
					mList.add("*" + eventArray.getString(j));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ScheduleAdapter adapter = new ScheduleAdapter(activity);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);

		mListView.setSelectionFromTop(mInitListPos, mInitListOffset);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		mTracker.send(new HitBuilders.EventBuilder().setCategory("add schedule").setAction("create")
				.build());
		new AlertDialog.Builder(activity).setTitle(R.string.schedule).setMessage(
				getString(R.string.add_cal_content, mList.get(position).split("\\) ")[1]))
				.setPositiveButton(R.string.determine, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mTracker.send(new HitBuilders.EventBuilder().setCategory("add schedule")
								.setAction("click").build());
						AddCalendarEvent(mList.get(position).substring(1));
					}
				}).setNegativeButton(R.string.cancel, null).show();
	}

	public void AddCalendarEvent(String Msg) {
		String _time = Msg.split("\\) ")[0].replace("(", "");
		String _msg = Msg.split("\\) ")[1];
		String _startTime;
		String _endTime;
		if (_time.contains("~")) {
			_startTime = _time.split("~")[0].replace(" ", "");
			_endTime = _time.split("~")[1].replace(" ", "");
		} else {
			_startTime = _time;
			_endTime = _time;
		}
		Intent calendarIntent =
				new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
		Calendar beginTime = Calendar.getInstance();
		Calendar endTime = Calendar.getInstance();
		beginTime.set(Calendar.getInstance().get(Calendar.YEAR),
				Integer.parseInt(_startTime.split("/")[0]) - 1,
				Integer.parseInt(_startTime.split("/")[1]), 0, 0, 0);
		endTime.set(Calendar.getInstance().get(Calendar.YEAR),
				Integer.parseInt(_endTime.split("/")[0]) - 1,
				Integer.parseInt(_endTime.split("/")[1]), 23, 59, 59);
		calendarIntent
				.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
		calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
		calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
		calendarIntent.putExtra(CalendarContract.Events.TITLE, _msg);
		calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "國立高雄應用科技大學");
		startActivity(calendarIntent);
	}

	public ListView getListView() {
		return mListView;
	}

	public class ScheduleAdapter extends BaseAdapter
			implements PinnedSectionListView.PinnedSectionListAdapter {
		private LayoutInflater inflater;

		private static final int TYPE_WEEK = 0, TYPE_SCHEDULE = 1;

		public ScheduleAdapter(Context context) {
			this.inflater =
					(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public String getItem(int position) {
			return mList.get(position);
		}

		@Override
		public int getViewTypeCount() {
			// TYPE_WEEK and TYPE_SCHEDULE
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
				holder.textView.setText(mList.get(position));
			} else {
				holder.textView.setText(mList.get(position).substring(1));
			}
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}
}