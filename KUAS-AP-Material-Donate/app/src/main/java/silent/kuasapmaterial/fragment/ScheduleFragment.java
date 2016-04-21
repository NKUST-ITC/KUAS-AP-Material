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

	String mScheduleData = "[{\"week\":\"寒\",\"events\":[\"(2/1) 104學年度第2學期開始日\",\"(2/1 ~ 2/5) " +
			"104學年度第1學期學業成績優良排名表送生輔組協查有無記過紀錄\",\"(2/2 ~ 2/4) 寄發104學年度第1學期學生成績單\",\"(2/3) " +
			"核發104學年度寒畢畢業證書\",\"(2/8 ~ 2/10) 春節放假\",\"(2/11) 補假一天(補2/7農曆除夕)\",\"(2/12) " +
			"調整放假\"]},{\"week\":\"預備週\",\"events\":[\"(2/15 ~ 2/19) " +
			"第1次公告104學年度第1學期學業成績優良名單\",\"(2/19) 研究生辦理104學年度第1學期離校手續截止日\",\"(2/19) " +
			"註冊繳費截止日\"]},{\"week\":\"第一週\",\"events\":[\"(2/22) 日間部、進修推廣處開學(上午正式上課)\",\"" +
			"(2/22) 網路公布104學年度第2學期加退選日程及相關辦法\",\"(2/22) 轉學生學分抵免申請開始\",\"(2/22) " +
			"輔系、雙主修申請開始\",\"(2/22) 校際選課申請開始\",\"(2/22) 研究生自完成註冊手續後開始辦理學位考試申請\",\"(2/22 ~ " +
			"2/26) 第2次公告104學年度第1學期學業成績優良名單\",\"(2/26 ~ 3/4) " +
			"通知104學年度第1學期學業成績優良學生至學校網站登載銀行帳號(限郵局及台灣企銀)\"]},{\"week\":\"第二週\"," +
			"\"events\":[\"(2/29) 補假一天(補2/28和平紀念日)\",\"(3/1) 校際選課截止日\",\"(3/1 ~ 3/8) " +
			"日間部學生加退選課申請作業(選課時間另行公布)\",\"(3/2) 輔系、雙主修申請截止日\",\"(3/4) 轉學生學分抵免申請截止日\"]}," +
			"{\"week\":\"第三週\",\"events\":[\"(3/9) 104學年度第2學期人工加掛選課申請截止日\"]}," +
			"{\"week\":\"第四週\",\"events\":[\"(3/14) 加退選結束教師自行列印點名單及成績冊(web)\",\"(3/16) " +
			"104學年度第2學期第1次教務會議\"]},{\"week\":\"第五週\",\"events\":[\"(3/24 ~ 3/30) " +
			"學生加退選課繳費\"]},{\"week\":\"第六週\",\"events\":[\"(3/28 ~ 4/1) " +
			"核算104學年度第2學期教師鐘點費\",\"(3/28 ~ 4/7) 教師上網登錄期中考考試時間\",\"(4/1) 學生逕修讀博士學位開始申請\"," +
			"\"(4/1) 學生辦理休、退學學雜費退 2/3 截止日\"]},{\"week\":\"第七週\",\"events\":[\"(4/4) " +
			"民族掃墓節放假一天\",\"(4/5) 補假一天(補4/3兒童節)\",\"(4/7 ~ 4/13) 104學年度暑修意願網路調查\"]}," +
			"{\"week\":\"第八週\",\"events\":[\"(4/11 ~ 4/23) 上網公布期中考考試時間、開放同學查詢\",\"(4/15) " +
			"學生逕修讀博士學位申請截止日\",\"(4/15) 教師期中考試卷申印製卷截止日\"]},{\"week\":\"第九週\"," +
			"\"events\":[\"(4/18 ~ 4/23) 日間部、進修推廣處期中考試\"]},{\"week\":\"第十週\"," +
			"\"events\":[\"(4/25) 104學年度第2學期停修課程開始申請\",\"(4/25) 登錄教師研究計畫案及義務授課減授時數\",\"" +
			"(4/25) 開放學生上網查詢期中成績暨期中預警科目\",\"(4/26) 公告104學年度暑修初步課表\",\"(4/30) " +
			"104學年度第2學期研究生學位考試申請期限截止日\",\"(4/30 ~ 5/1) 105學年度四技統一入學測驗考試\"]}," +
			"{\"week\":\"第十一週\",\"events\":[\"(5/1) 105學年度二技統一入學測驗考試\",\"(5/1) 日間部大學部學生轉系" +
			"(組)開始申請\",\"(5/2 ~ 5/6) 104學年度暑修網路選課\",\"(5/4) 教師登錄期中成績暨預警作業截止日\",\"(5/6 ~ " +
			"5/9) 發放期中預警學生名單予各系、班級導師、任課老師\"]},{\"week\":\"第十二週\",\"events\":[\"(5/9 ~ " +
			"5/29) 導師填報期中預警輔導紀錄表(web)\",\"(5/13) 學生辦理休、退學學雜費退 1/3 截止日\"]}," +
			"{\"week\":\"第十三週\",\"events\":[\"(5/15) 104學年度第2學期停修課程申請截止日\",\"(5/15) " +
			"日間部大學部學生轉系(組)申請期限截止日\",\"(5/16 ~ 5/18) 寄發105學年度第1學期復學通知\",\"(5/18) " +
			"104學年度第2學期第2次教務會議\"]},{\"week\":\"第十四週\",\"events\":[\"(5/23 ~ 5/25) " +
			"核算停修後教師鐘點費\"]},{\"week\":\"第十五週\",\"events\":[\"(5/30) 發放舊生註冊須知\",\"(5/30 ~ " +
			"6/7) 教師上網登錄期末考時間\",\"(5/30 ~ 6/3) 104學年度暑修繳費\",\"(6/1) " +
			"開放上網查詢105學年度第1學期課程表\",\"(6/3 ~ 6/8) 日間部學生105學年度第1學期選課登記志願(初選第一階段)\",\"(6/4) " +
			"補行上班上課\"]},{\"week\":\"第十六週\",\"events\":[\"(6/9) 端午節放假一天\",\"(6/10) " +
			"調整放假\"]},{\"week\":\"第十七週\",\"events\":[\"(6/13 ~ 6/25) 上網公佈期末考時間，開放學生查詢\"," +
			"\"(6/14 ~ 6/21) 日間部學生105學年度第1學期選課電腦篩選\",\"(6/17) 學生期末考扣考資料通知學生、家長、老師\",\"" +
			"(6/17) 教師期末考試卷申印製卷截止日\"]},{\"week\":\"第十八週\",\"events\":[\"(6/20 ~ 6/25) " +
			"日間部、進修推廣處期末考試\",\"(6/22 ~ 6/24) 日間部學生105學年度第1學期選課電腦篩選分發結果公告\"]}," +
			"{\"week\":\"第十九週\",\"events\":[\"(6/27 ~ 6/30) 日間部學生105學年度第1學期初選第二階段選課\",\"" +
			"(6/29) 教師送交畢業班學期考試成績截止日\"]},{\"week\":\"暑\",\"events\":[\"(7/3) " +
			"教師送交104學年度第2學期學生學期成績截止日\",\"(7/4 ~ 7/6) 本學期修課不及格者辦理暑修報名並同時繳費\",\"(7/4 ~ 7/6)" +
			" 外校生至本校暑修選課報名並同時繳費\",\"(7/11) 暑修開始上課，預計8/20上課結束\",\"(7/11) " +
			"寄發104學年度第2學期退學通知\",\"(7/18) 核發104學年度畢業生畢業證書\",\"(7/18 ~ 7/20) " +
			"寄發104學年度第2學期學生成績單\",\"(7/31) 104學年度第2學期研究生學位考試截止日\"]}]";

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