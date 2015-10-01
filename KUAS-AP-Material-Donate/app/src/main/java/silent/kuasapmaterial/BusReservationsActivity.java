package silent.kuasapmaterial;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.analytics.HitBuilders;
import com.kuas.ap.donate.R;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.callback.BusReservationsCallback;
import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.libs.ProgressWheel;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.models.BusModel;

public class BusReservationsActivity extends SilentActivity
		implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

	ListView mListView;
	TextView mNoReservationTextView;
	LinearLayout mNoReservationLinearLayout;
	ProgressWheel mProgressWheel;
	SwipeRefreshLayout mSwipeRefreshLayout;

	List<BusModel> mList;
	private int mInitListPos = 0, mInitListOffset = 0;
	BusAdapter mAdapter;
	boolean isRefresh = false, isRetry = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.push_up_in, R.anim.hold);
		setContentView(R.layout.activity_bus_reservations);
		setUpToolBar(getString(R.string.bus));

		initGA("Bus Reservations Screen");
		restoreArgs(savedInstanceState);
		findViews();
		setUpViews();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("isRefresh", isRefresh);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void finish() {
		super.finish();

		overridePendingTransition(R.anim.hold, R.anim.slide_down_out);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return false;
	}

	private void restoreArgs(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mInitListPos = savedInstanceState.getInt("mInitListPos");
			mInitListOffset = savedInstanceState.getInt("mInitListOffset");

			if (savedInstanceState.containsKey("mList")) {
				mList = new Gson().fromJson(savedInstanceState.getString("mList"),
						new TypeToken<List<BusModel>>() {
						}.getType());
			}
		}

		if (mList == null) {
			mList = new ArrayList<>();
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
		if (mList != null) {
			outState.putString("mList", new Gson().toJson(mList));
		}
	}

	private void findViews() {
		mListView = (ListView) findViewById(R.id.listView);
		mNoReservationTextView = (TextView) findViewById(R.id.textView_no_reservation);
		mNoReservationLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_no_reservation);
		mProgressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
	}

	private void setUpViews() {
		mListView.setOnItemClickListener(this);
		mListView.setDividerHeight(5);
		mAdapter = new BusAdapter(this);
		mListView.setAdapter(mAdapter);

		mListView.setSelectionFromTop(mInitListPos, mInitListOffset);
		mNoReservationLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRetry) {
					mTracker.send(
							new HitBuilders.EventBuilder().setCategory("retry").setAction("click")
									.build());
					isRetry = false;
					getData();
				}
			}
		});
		setUpPullRefresh();

		if (mList.size() > 0) {
			mProgressWheel.setVisibility(View.GONE);
			mNoReservationLinearLayout.setVisibility(View.GONE);
		} else {
			getData();
		}
	}

	@Override
	public void onRefresh() {
		mTracker.send(
				new HitBuilders.EventBuilder().setCategory("refresh").setAction("swipe").build());
		mSwipeRefreshLayout.setRefreshing(true);
		isRetry = false;
		getData();
	}

	private void setUpPullRefresh() {
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeColors(Utils.getSwipeRefreshColors(this));
	}

	private void getData() {
		mProgressWheel.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.GONE);
		mNoReservationLinearLayout.setVisibility(View.GONE);
		mSwipeRefreshLayout.setEnabled(false);

		Helper.getBusReservations(this, new BusReservationsCallback() {

			@Override
			public void onSuccess(List<BusModel> modelList) {
				super.onSuccess(modelList);

				Utils.saveBusNotify(BusReservationsActivity.this, modelList);

				mList = modelList;
				mListView.setVisibility(View.VISIBLE);
				mProgressWheel.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();

				if (modelList.size() == 0) {
					mNoReservationTextView
							.setText(getString(R.string.bus_no_reservation, "\uD83D\uDE06"));
					mNoReservationLinearLayout.setVisibility(View.VISIBLE);
				} else {
					mNoReservationLinearLayout.setVisibility(View.GONE);
				}

				mSwipeRefreshLayout.setEnabled(true);
				mSwipeRefreshLayout.setRefreshing(false);
			}

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);

				isRetry = true;

				mListView.setVisibility(View.VISIBLE);
				mProgressWheel.setVisibility(View.GONE);
				mNoReservationTextView.setText(R.string.click_to_retry);
				mNoReservationLinearLayout.setVisibility(View.VISIBLE);
				mList.clear();
				mAdapter.notifyDataSetChanged();

				mSwipeRefreshLayout.setEnabled(true);
				mSwipeRefreshLayout.setRefreshing(false);
			}

			@Override
			public void onTokenExpired() {
				super.onTokenExpired();
				Utils.createTokenExpired(BusReservationsActivity.this).show();
				mTracker.send(
						new HitBuilders.EventBuilder().setCategory("token").setAction("expired")
								.build());
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		mTracker.send(new HitBuilders.EventBuilder().setCategory("cancel bus").setAction("create")
				.build());
		boolean index = mList.get(position).endStation.equals("燕巢");
		new AlertDialog.Builder(this).setTitle(R.string.bus_cancel_reserve_confirm_title)
				.setMessage(getString(R.string.bus_cancel_reserve_confirm_content,
						getString(index ? R.string.bus_from_jiangong : R.string.bus_from_yanchao),
						mList.get(position).Time)).setPositiveButton(R.string.bus_cancel_reserve,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mTracker.send(new HitBuilders.EventBuilder().setCategory("cancel bus")
								.setAction("click").build());
						cancelBookBus(mList, position);
					}
				}).setNegativeButton(R.string.back, null).show();
	}

	private void cancelBookBus(final List<BusModel> modelList, final int position) {
		isRefresh = true;
		Helper.cancelBookingBus(BusReservationsActivity.this, modelList.get(position).cancelKey,
				new GeneralCallback() {

					@Override
					public void onSuccess() {
						super.onSuccess();
						mTracker.send(new HitBuilders.EventBuilder().setCategory("cancel bus")
								.setAction("status").setLabel("success").build());
						getData();
						Toast.makeText(BusReservationsActivity.this,
								R.string.bus_cancel_reserve_success, Toast.LENGTH_LONG).show();
					}

					@Override
					public void onFail(String errorMessage) {
						super.onFail(errorMessage);
						mTracker.send(new HitBuilders.EventBuilder().setCategory("cancel bus")
								.setAction("status").setLabel("fail " + errorMessage).build());
						Toast.makeText(BusReservationsActivity.this, R.string.something_error,
								Toast.LENGTH_LONG).show();
					}

					@Override
					public void onTokenExpired() {
						super.onTokenExpired();
						Utils.createTokenExpired(BusReservationsActivity.this).show();
					}
				});
	}

	public class BusAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public BusAdapter(Context context) {
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
		public BusModel getItem(int position) {
			return mList.get(position);
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
				convertView = inflater.inflate(R.layout.list_bus_reservations, parent, false);
				holder.textView_location =
						(TextView) convertView.findViewById(R.id.textView_location);
				holder.textView_time = (TextView) convertView.findViewById(R.id.textView_time);
				holder.textView_date = (TextView) convertView.findViewById(R.id.textView_date);
				holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (mList.get(position).endStation.equals("燕巢")) {
				holder.linearLayout.setBackgroundColor(getResources().getColor(R.color.blue_600));
				holder.textView_location.setText(getString(R.string.bus_jiangong_reservations));

			} else {
				holder.linearLayout.setBackgroundColor(getResources().getColor(R.color.green_600));
				holder.textView_location.setText(getString(R.string.bus_yanchao_reservations));
			}
			holder.textView_date.setText(mList.get(position).Time.split(" ")[0]);
			holder.textView_time.setText(mList.get(position).Time.split(" ")[1]);
			return convertView;
		}

		class ViewHolder {
			TextView textView_location;
			TextView textView_time;
			TextView textView_date;
			LinearLayout linearLayout;
		}
	}
}
