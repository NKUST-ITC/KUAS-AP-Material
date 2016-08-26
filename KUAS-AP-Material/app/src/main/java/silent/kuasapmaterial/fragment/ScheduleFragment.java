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
import com.kuas.ap.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
			"[{\"week\":\"暑\",\"events\":[\"(8/1) 105學年度第 1 學期開始日\",\"(8/8 ~ 8/10) 寄發二技、四技、研究所新生入學資料袋\",\"(8/15 ~ 8/19) 104學年度第2學期學業成績優良排名表送生輔組協查有無記過紀錄\",\"(8/20) 104學年度暑修課程結束日\",\"(8/22 ~ 8/26) 105學年度第1學期日間部研究所新生初選上網選課\",\"(8/24) 教師送交104學年度暑修課程成績截止日\",\"(8/25 ~ 8/31) 第1次公告104學年度第2學期學業成績優良名單\",\"(8/29 ~ 8/31) 105學年度第1學期日間部四技新生初選上網選課\",\"(9/1 ~ 9/2) 105學年度第1學期日間部二技新生初選上網選課\",\"(9/1 ~ 9/7) 第2次公告104學年度第2學期學業成績優良名單\",\"(9/1 ~ 9/19) 碩士班甄試及考試系所分則調查\"]},{\"week\":\"預備週\",\"events\":[\"(9/5) 核發參加104學年度暑修後可畢業學生之畢業證書\",\"(9/5) 網路公佈日間部學生加退選日程及相關辦法\",\"(9/6) (9/6)105學年度第1學期新進教師研習\",\"(9/8 ~ 9/14) 通知學業成績優良學生至學校網站填寫銀行帳號\",\"(9/9) 研究生辦理104學年度第2學期離校手續截止日\",\"(9/9) 註冊繳費截止日\",\"(9/10) 補行上班(補9/16 之彈性放假)\"]},{\"week\":\"第一週\",\"events\":[\"(9/12) 日間部、進修推廣處開學典禮(上午正式上課)\",\"(9/12) 發放新生學生證\",\"(9/12 ~ 9/20) 輔系、雙主修、新生學分抵免開始申請\",\"(9/12 ~ 9/20) 校際選課開始申請\",\"(9/12) 研究生自完成註冊手續後開始辦理學位考試申請\",\"(9/14) 招生研討會\",\"(9/15) 中秋節放假一天\",\"(9/16) 調整放假\"]},{\"week\":\"第二週\",\"events\":[\"(9/19) 日間部研究生加退選課(選課時間另行公告)\",\"(9/19 ~ 9/23) 日間部大學部學生加退選課(選課時間另行公告)\",\"(9/19 ~ 9/30) 碩士班甄試招生簡章公告（預計）\",\"(9/19 ~ 12/9) 碩士班甄試招生期（預計）\",\"(9/19 ~ 9/23) 教師上網登錄期末考考試地點\",\"(9/20) 輔系、雙主修申請截止日\",\"(9/20) 校際選課截止日\",\"(9/21 ~ 9/23) 核算停修後教師鐘點費\",\"(9/23) 新生學分抵免申請截止日\"]},{\"week\":\"第三週\",\"events\":[\"(9/27) 日間部學生人工加掛選課申請截止日\",\"(9/27) 公告學生上網確認選課清單時間\"]},{\"week\":\"第四週\",\"events\":[\"(10/3 ~ 10/11) 日間部學生上網確認選課清單\",\"(10/3 ~ 10/31) 博士班簡章系所分則調查\",\"(10/5) (10/5)105學年度第1學期第1次教務會議\"]},{\"week\":\"第五週\",\"events\":[\"(10/10) 國慶日放假一天\",\"(10/11 ~ 10/14) 教師鐘點費核發作業\"]},{\"week\":\"第六週\",\"events\":[\"(10/17 ~ 10/21) 教師上網登錄期中考考試地點\",\"(10/21 ~ 10/25) 日間部學生辦理加退選繳費\",\"(10/21) 學生辦理休、退學學雜費退2/3截止日\"]},{\"week\":\"第七週\",\"events\":[\"(10/23) 校運會\"]},{\"week\":\"第八週\",\"events\":[\"(10/30) 校慶\",\"(10/31) 上網公布期中考考試時間、開放同學查詢\",\"(10/31 ~ 11/4) 教師期中考試卷申印製卷\",\"(11/1 ~ 11/14) 碩士班考試簡章公告（預計）\",\"(11/1 ~ 4/28) 碩士班考試招生期（預計）\"]},{\"week\":\"第九週\",\"events\":[\"(11/7 ~ 11/12) 日間部、進修推廣處期中考試\"]},{\"week\":\"第十週\",\"events\":[\"(11/14) 105學年度第1學期停修課程開始申請\",\"(11/14) 登錄教師研究計畫案及義務授課減授時數\"]},{\"week\":\"第十一週\",\"events\":[\"(11/23) 教師登錄學生期中預警作業截止日\",\"(11/24 ~ 11/28) 發放期中預警學生名單予各系、班級導師、任課老師\"]},{\"week\":\"第十二週\",\"events\":[\"(11/30) 105學年度第1學期研究生學位考試申請期限截止日\",\"(12/1 ~ 12/16) 博士班招生簡章公告（預計）\",\"(12/2) 學生辦理休、退學學雜費退1/3截止日\"]},{\"week\":\"第十三週\",\"events\":[\"(12/7) (12/7)105學年度第1學期第2次教務會議\"]},{\"week\":\"第十四週\",\"events\":[\"(12/15) 105學年度第1學期停修課程申請截止日\"]},{\"week\":\"第十五週\",\"events\":[\"(12/19) 寄發105學年度第2學期復學通知\"]},{\"week\":\"第十六週\",\"events\":[\"(12/26) 發放105學年度第2學期舊生註冊須知\",\"(12/28) 開放上網查詢105學年度第2學期課程表\",\"(12/30 ~ 1/5) 日間部105學年度第2學期初選第一階【網路線上登記志願】\"]},{\"week\":\"第十七週\",\"events\":[\"(1/1) 開國紀念日\",\"(1/2) 補假一天(補1/1 開國紀念日)\",\"(1/3) 開放學生上網查詢期末考時間\",\"(1/5) 學生期末考扣考資料通知學生、家長、老師\",\"(1/5) 教師期末考試卷申印製卷截止日\",\"(1/6 ~ 1/12) 日間部105學年度第2學期初選第一階【電腦自動篩選】\"]},{\"week\":\"第十八週\",\"events\":[\"(1/9 ~ 1/14) 日間部、進修推廣處期末考試\",\"(1/13 ~ 1/15) 日間部105學年度第2學期初選第一階【電腦篩選結果公告】\"]},{\"week\":\"第十九週\",\"events\":[\"(1/16) 開放學生上網查詢學期成績\",\"(1/16 ~ 1/20) 日間部105學年度第2學期選課初選第二階【網路即時選課】\"]},{\"week\":\"第二十週\",\"events\":[\"(1/22) 教師送交105學年度第1學期學期成績截止日\",\"(1/27) 除夕\",\"(1/28 ~ 1/30) 春節\"]},{\"week\":\"第二十一週\",\"events\":[\"(1/31) 補假一天(年初一遇例假日補假)\",\"(1/31) 105學年度第1學期結束日\",\"(1/31) 105學年度第1學期研究生學位考試截止日\",\"(2/1) 補假一天(年初二遇例假日補假)\"]}]";

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
					if (mInitListPos == 0) {
						try {
							Calendar calendar = Calendar.getInstance();
							Date now = new Date(System.currentTimeMillis());
							String _time = eventArray.getString(j).split("\\) ")[0].substring(1);
							String _startTime =
									_time.contains("~") ? _time.split("~")[1].trim() : _time;
							calendar.set(Calendar.getInstance().get(Calendar.YEAR),
									Integer.parseInt(_startTime.split("/")[0]) - 1,
									Integer.parseInt(_startTime.split("/")[1]));
							if (calendar.getTime().after(now)) {
								mInitListPos = mList.size() - 5 < 0 ? 0 : mList.size() - 5;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ScheduleAdapter adapter = new ScheduleAdapter(activity);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);

		((ListView) mListView).setSelectionFromTop(mInitListPos, mInitListOffset);
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
		String _time = Msg.split("\\) ")[0].substring(1);
		String _msg = Msg.split("\\) ")[1];
		String _startTime;
		String _endTime;
		if (_time.contains("~")) {
			_startTime = _time.split("~")[0].trim();
			_endTime = _time.split("~")[1].trim();
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