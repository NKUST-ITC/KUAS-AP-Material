package silent.kuasapmaterial;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.callback.BusCallback;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.libs.ProgressWheel;
import silent.kuasapmaterial.libs.segmentcontrol.SegmentControl;
import silent.kuasapmaterial.models.BusModel;

public class BusActivity extends SilentActivity
		implements NavigationView.OnNavigationItemSelectedListener,
		SegmentControl.OnSegmentControlClickListener, AdapterView.OnItemClickListener,
		DatePickerDialog.OnDateSetListener {

	SegmentControl mSegmentControl;
	ListView mListView;
	TextView mTextView;
	ProgressWheel mProgressWheel;

	String mDate;
	List<BusModel> mJianGongList, mYanChaoList;
	private int mInitListPos = 0, mInitListOffset = 0, mIndex = 0;
	BusAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus);
		init(R.string.bus, this, R.id.nav_bus);

		restoreArgs(savedInstanceState);
		findViews();
		setUpViews();
	}

	// TODO Wait for handle navigation items
	@Override
	public boolean onNavigationItemSelected(MenuItem menuItem) {
		drawer.closeDrawers();
		if (menuItem.isChecked()) {
			return true;
		}
		return true;
	}

	private void restoreArgs(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mDate = savedInstanceState.getString("mDate");
			mIndex = savedInstanceState.getInt("mIndex");
			mInitListPos = savedInstanceState.getInt("mInitListPos");
			mInitListOffset = savedInstanceState.getInt("mInitListOffset");

			if (savedInstanceState.containsKey("mJianGongList")) {
				mJianGongList = new Gson().fromJson(savedInstanceState.getString("mJianGongList"),
						new TypeToken<List<BusModel>>() {
						}.getType());
			}
			if (savedInstanceState.containsKey("mYanChaoList")) {
				mYanChaoList = new Gson().fromJson(savedInstanceState.getString("mYanChaoList"),
						new TypeToken<List<BusModel>>() {
						}.getType());
			}
		} else {
			showDatePickerDialog();
		}

		if (mJianGongList == null) {
			mJianGongList = new ArrayList<>();
		}
		if (mYanChaoList == null) {
			mYanChaoList = new ArrayList<>();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString("mDate", mDate);
		outState.putInt("mIndex", mIndex);
		if (mListView != null) {
			outState.putInt("mInitListPos", mListView.getFirstVisiblePosition());
			View vNewTop = mListView.getChildAt(0);
			outState.putInt("mInitListOffset", (vNewTop == null) ? 0 : vNewTop.getTop());
		}
		if (mJianGongList != null) {
			outState.putString("mJianGongList", new Gson().toJson(mJianGongList));
		}
		if (mYanChaoList != null) {
			outState.putString("mYanChaoList", new Gson().toJson(mYanChaoList));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		DatePickerDialog dpd =
				(DatePickerDialog) getFragmentManager().findFragmentByTag("DatePickerDialog");

		if (dpd != null) {
			dpd.setOnDateSetListener(this);
		}
	}

	private void findViews() {
		mSegmentControl = (SegmentControl) findViewById(R.id.segment_control);
		mListView = (ListView) findViewById(R.id.listView);
		mTextView = (TextView) findViewById(R.id.textView_pickDate);
		mProgressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
	}

	private void setUpViews() {
		mSegmentControl.setmOnSegmentControlClickListener(this);
		mListView.setOnItemClickListener(this);
		mAdapter = new BusAdapter(this);
		mListView.setAdapter(mAdapter);

		if (mDate != null && mDate.length() > 0) {
			mTextView.setText(getString(R.string.bus_pick_date, mDate));
		}

		mSegmentControl.setIndex(mIndex);
		changeSegmentColor();

		mListView.setSelectionFromTop(mInitListPos, mInitListOffset);
		mTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog();
			}
		});

		if (mJianGongList.size() > 0 || mYanChaoList.size() > 0) {
			mProgressWheel.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
	}

	private void showDatePickerDialog() {
		Calendar now = Calendar.getInstance();
		DatePickerDialog dpd = DatePickerDialog
				.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
						now.get(Calendar.DAY_OF_MONTH));
		dpd.setThemeDark(false);
		dpd.vibrate(false);
		dpd.dismissOnPause(false);
		dpd.show(getFragmentManager(), "DatePickerDialog");
	}

	private void getData() {
		mProgressWheel.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.GONE);

		Helper.getBusQuery(this, mDate, new BusCallback() {

			@Override
			public void onSuccess(List<BusModel> jiangongList, List<BusModel> yanchaoList) {
				super.onSuccess(jiangongList, yanchaoList);

				mJianGongList = jiangongList;
				mYanChaoList = yanchaoList;
				mProgressWheel.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);

				mProgressWheel.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void onSegmentControlClick(int index) {
		mIndex = index;
		mAdapter.notifyDataSetChanged();

		changeSegmentColor();
		mListView.smoothScrollToPosition(0);
	}

	@Override
	public void onSegmentControlReselect() {
		mListView.smoothScrollToPosition(0);
	}

	private void changeSegmentColor() {
		int[][] states = new int[][]{new int[]{android.R.attr.state_enabled}};
		int[] JianGongColors = new int[]{getResources().getColor(R.color.blue_600)};
		int[] YanChaoColors = new int[]{getResources().getColor(R.color.green_600)};
		if (mIndex == 0) {
			mSegmentControl.setColors(new ColorStateList(states, JianGongColors));
		} else {
			mSegmentControl.setColors(new ColorStateList(states, YanChaoColors));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		mDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
		mTextView.setText(getString(R.string.bus_pick_date, mDate));
		getData();
	}

	public class BusAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public BusAdapter(Context context) {
			this.inflater =
					(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mIndex == 0 ? mJianGongList.size() : mYanChaoList.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public BusModel getItem(int position) {
			return mIndex == 0 ? mJianGongList.get(position) : mYanChaoList.get(position);
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.list_bus, parent, false);
				holder.textView_location =
						(TextView) convertView.findViewById(R.id.textView_location);
				holder.textView_time = (TextView) convertView.findViewById(R.id.textView_time);
				holder.textView_count = (TextView) convertView.findViewById(R.id.textView_count);
				holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (mIndex == 0) {
				if (mJianGongList.get(position).isReserve) {
					holder.linearLayout
							.setBackgroundColor(getResources().getColor(R.color.red_600));
					holder.textView_location.setText(getString(R.string.bus_jiangong_reserved));
				} else {
					holder.linearLayout
							.setBackgroundColor(getResources().getColor(R.color.blue_600));
					holder.textView_location.setText(getString(R.string.bus_jiangong));
				}
				holder.textView_count.setText(
						getString(R.string.bus_count, mJianGongList.get(position).reserveCount,
								mJianGongList.get(position).limitCount));
				holder.textView_time.setText(mJianGongList.get(position).Time);
			} else {
				if (mYanChaoList.get(position).isReserve) {
					holder.linearLayout
							.setBackgroundColor(getResources().getColor(R.color.red_600));
					holder.textView_location.setText(getString(R.string.bus_yanchao_reserved));
				} else {
					holder.linearLayout
							.setBackgroundColor(getResources().getColor(R.color.green_600));
					holder.textView_location.setText(getString(R.string.bus_yanchao));
				}
				holder.textView_count.setText(
						getString(R.string.bus_count, mYanChaoList.get(position).reserveCount,
								mYanChaoList.get(position).limitCount));
				holder.textView_time.setText(mYanChaoList.get(position).Time);
			}
			return convertView;
		}

		class ViewHolder {
			TextView textView_location;
			TextView textView_time;
			TextView textView_count;
			LinearLayout linearLayout;
		}
	}
}
