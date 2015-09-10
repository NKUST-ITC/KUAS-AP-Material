package silent.kuasapmaterial;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.callback.BusCallback;
import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.libs.ListScrollDistanceCalculator;
import silent.kuasapmaterial.libs.ProgressWheel;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.libs.segmentcontrol.SegmentControl;
import silent.kuasapmaterial.models.BusModel;

public class BusActivity extends SilentActivity
		implements NavigationView.OnNavigationItemSelectedListener,
		SegmentControl.OnSegmentControlClickListener, AdapterView.OnItemClickListener,
		DatePickerDialog.OnDateSetListener, ListScrollDistanceCalculator.ScrollDistanceListener,
		SwipeRefreshLayout.OnRefreshListener {

	SegmentControl mSegmentControl;
	ListView mListView;
	TextView mTextView;
	TextView mNoBusTextView;
	LinearLayout mNoBusLinearLayout;
	ProgressWheel mProgressWheel;
	FloatingActionButton mFab;
	SwipeRefreshLayout mSwipeRefreshLayout;

	String mDate;
	List<BusModel> mJianGongList, mYanChaoList;
	private int mInitListPos = 0, mInitListOffset = 0, mIndex = 0;
	BusAdapter mAdapter;
	ListScrollDistanceCalculator mListScrollDistanceCalculator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		setContentView(R.layout.activity_bus);
		init(R.string.bus, this, R.id.nav_bus);

		restoreArgs(savedInstanceState);
		findViews();
		setUpViews();
	}

	@Override
	public void finish() {
		super.finish();

		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case Constant.REQUEST_BUS_RESERVATIONS:
				if (resultCode == RESULT_OK && data != null) {
					if (data.hasExtra("isRefresh") && data.getExtras().getBoolean("isRefresh")) {
						getData();
					}
				}
				break;
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
		mFab = (FloatingActionButton) findViewById(R.id.fab);
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
		mNoBusLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_no_bus);
		mNoBusTextView = (TextView) findViewById(R.id.textView_no_bus);
	}

	private void setUpViews() {
		mSegmentControl.setmOnSegmentControlClickListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setDividerHeight(5);
		mAdapter = new BusAdapter(this);
		mListView.setAdapter(mAdapter);
		mListScrollDistanceCalculator = new ListScrollDistanceCalculator();
		mListScrollDistanceCalculator.setScrollDistanceListener(this);
		mListView.setOnScrollListener(mListScrollDistanceCalculator);
		mFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(BusActivity.this, BusReservationsActivity.class),
						Constant.REQUEST_BUS_RESERVATIONS);
			}
		});

		mSegmentControl.setIndex(mIndex);
		setUpSegmentColor();
		setUpPullRefresh();
		mNoBusTextView.setText(getString(R.string.bus_no_bus, "\uD83D\uDE0B"));

		mListView.setSelectionFromTop(mInitListPos, mInitListOffset);
		mTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog();
			}
		});
		mNoBusLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog();
			}
		});

		if (mDate != null && mDate.length() > 0) {
			mTextView.setText(getString(R.string.bus_pick_date, mDate));
		} else {
			mSwipeRefreshLayout.setEnabled(false);
		}

		setUpListView();
	}

	@Override
	public void onRefresh() {
		if (mDate == null || mDate.length() == 0) {
			return;
		}
		mSwipeRefreshLayout.setRefreshing(true);
		getData();
	}

	private void setUpPullRefresh() {
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeColors(Utils.getSwipeRefreshColors(this));
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
		mNoBusLinearLayout.setVisibility(View.GONE);
		mFab.setEnabled(false);
		mSwipeRefreshLayout.setEnabled(false);
		mFab.hide();

		Helper.getBusTimeTable(this, mDate, new BusCallback() {

			@Override
			public void onSuccess(List<BusModel> jiangongList, List<BusModel> yanchaoList) {
				super.onSuccess(jiangongList, yanchaoList);

				mJianGongList = jiangongList;
				mYanChaoList = yanchaoList;
				setUpListView();
				mAdapter.notifyDataSetChanged();

				mFab.setEnabled(true);
				mFab.show();
				mSwipeRefreshLayout.setEnabled(true);
				mSwipeRefreshLayout.setRefreshing(false);
			}

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);

				mJianGongList.clear();
				mYanChaoList.clear();
				setUpListView();
				mAdapter.notifyDataSetChanged();

				mFab.setEnabled(true);
				mFab.show();
				mSwipeRefreshLayout.setEnabled(true);
				mSwipeRefreshLayout.setRefreshing(false);
			}
		});
	}

	@Override
	public void onSegmentControlClick(int index) {
		mIndex = index;
		mAdapter.notifyDataSetChanged();

		setUpSegmentColor();
		setUpListView();
		mListView.smoothScrollToPosition(0);

		if (!mFab.isShown() && mFab.isEnabled()) {
			mFab.show();
		}
	}

	@Override
	public void onSegmentControlReselect() {
		mListView.smoothScrollToPosition(0);

		if (!mFab.isShown() && mFab.isEnabled()) {
			mFab.show();
		}
	}

	private void setUpSegmentColor() {
		if (mIndex == 0) {
			mSegmentControl
					.setColors(ColorStateList.valueOf(getResources().getColor(R.color.blue_600)));
		} else {
			mSegmentControl
					.setColors(ColorStateList.valueOf(getResources().getColor(R.color.green_600)));
		}
		setUpFabColor();
	}

	private void setUpFabColor() {
		if (mIndex == 0) {
			mFab.setBackgroundTintList(
					ColorStateList.valueOf(getResources().getColor(R.color.green_600)));
		} else {
			mFab.setBackgroundTintList(
					ColorStateList.valueOf(getResources().getColor(R.color.blue_600)));
		}
	}

	private void setUpListView() {
		mProgressWheel.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
		int count = mIndex == 0 ? mJianGongList.size() : mYanChaoList.size();
		if (count == 0) {
			mNoBusLinearLayout.setVisibility(View.VISIBLE);
		} else {
			mNoBusLinearLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		final List<BusModel> modelList = mIndex == 0 ? mJianGongList : mYanChaoList;
		if (modelList.get(position).isReserve) {
			new AlertDialog.Builder(this).setTitle(R.string.bus_cancel_reserve_confirm_title)
					.setMessage(getString(R.string.bus_cancel_reserve_confirm_content, getString(
									mIndex == 0 ? R.string.bus_from_jiangong :
											R.string.bus_from_yanchao),
							modelList.get(position).Time))
					.setPositiveButton(R.string.bus_cancel_reserve,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									cancelBookBus(modelList, position);
								}
							}).setNegativeButton(R.string.back, null).show();
		} else {
			new AlertDialog.Builder(this).setTitle(R.string.bus_reserve_confirm_title).setMessage(
					getString(R.string.bus_reserve_confirm_content, getString(
									mIndex == 0 ? R.string.bus_from_jiangong :
											R.string.bus_from_yanchao),
							modelList.get(position).Time))
					.setPositiveButton(R.string.bus_reserve, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							bookBus(modelList, position);
						}
					}).setNegativeButton(R.string.cancel, null).show();
		}
	}

	private void cancelBookBus(List<BusModel> modelList, final int position) {
		Helper.cancelBookingBus(BusActivity.this, modelList.get(position).cancelKey,
				new GeneralCallback() {

					@Override
					public void onSuccess() {
						super.onSuccess();
						if (mIndex == 0) {
							mJianGongList.get(position).isReserve = false;
						} else {
							mYanChaoList.get(position).isReserve = false;
						}
						mAdapter.notifyDataSetChanged();
						Toast.makeText(BusActivity.this, R.string.bus_cancel_reserve_success,
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFail(String errorMessage) {
						super.onFail(errorMessage);
						Toast.makeText(BusActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void bookBus(List<BusModel> modelList, final int position) {
		Helper.bookingBus(BusActivity.this, modelList.get(position).busId, new GeneralCallback() {

			@Override
			public void onSuccess() {
				super.onSuccess();
				if (mIndex == 0) {
					mJianGongList.get(position).isReserve = true;
				} else {
					mYanChaoList.get(position).isReserve = true;
				}
				mAdapter.notifyDataSetChanged();
				Toast.makeText(BusActivity.this, R.string.bus_reserve_success, Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);
				Toast.makeText(BusActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		mDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
		mTextView.setText(getString(R.string.bus_pick_date, mDate));
		getData();
	}

	@Override
	public void onScrollDistanceChanged(int delta, int total) {
		if (delta > 10 && mFab.isEnabled()) {
			mFab.show();
		} else if (delta < -10) {
			mFab.hide();
		}
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
