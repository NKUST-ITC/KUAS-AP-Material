package silent.kuasapmaterial.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kuas.ap.R;

import java.util.ArrayList;
import java.util.List;

import silent.kuasapmaterial.base.SilentFragment;
import silent.kuasapmaterial.callback.NotificationCallback;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.libs.PinnedSectionListView;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.models.NotificationModel;

public class NotificationFragment extends SilentFragment
		implements PinnedSectionListView.OnBottomReachedListener,
		SwipeRefreshLayout.OnRefreshListener {

	private View view;
	private PinnedSectionListView mListView;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	List<NotificationModel> mList;
	Activity activity;

	private int mPage, mInitListPos = 0, mInitListOffset = 0;
	private boolean isLoadingPosts = false, isRetry = false;
	Adapter mAdapter;

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
			mPage = savedInstanceState.getInt("mPage");
			mInitListPos = savedInstanceState.getInt("mInitListPos");
			mInitListOffset = savedInstanceState.getInt("mInitListOffset");

			if (savedInstanceState.containsKey("mList")) {
				mList = new Gson().fromJson(savedInstanceState.getString("mList"),
						new TypeToken<List<NotificationModel>>() {
						}.getType());
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("mPage", isLoadingPosts ? mPage - 1 : mPage);
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
		mListView = (PinnedSectionListView) view.findViewById(R.id.listView);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
	}

	private void setUpViews() {
		mAdapter = new Adapter(activity);
		if (mList != null && mList.size() > 0) {
			mListView.setSelectionFromTop(mInitListPos, mInitListOffset);
			mAdapter.notifyDataSetChanged();
		} else {
			mList = new ArrayList<>();
			getNotificationData(true);
		}
		mListView.setAdapter(mAdapter);
		mListView.setOnBottomReachedListener(this);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mTracker.send(new HitBuilders.EventBuilder().setCategory("notification link")
						.setAction("click").build());
				if (position < mList.size()) {
					if (mList.get(position).link.startsWith("http")) {
						Intent browserIntent =
								new Intent(Intent.ACTION_VIEW, Uri.parse(mList.get(position).link));
						startActivity(browserIntent);
					}
				} else {
					isRetry = false;
					getNotificationData(false);
				}
			}
		});
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			                               long id) {
				String shareData =
						mList.get(position).content + "\n" + mList.get(position).link + "\n\n" +
								getString(R.string.send_from);

				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, shareData);
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, getString(R.string.share_to)));

				mTracker.send(new HitBuilders.EventBuilder().setCategory("share").setAction("click")
						.setLabel(shareData).build());
				return true;
			}
		});
		setUpPullRefresh();
	}

	@Override
	public void onRefresh() {
		if (!isLoadingPosts) {
			mTracker.send(new HitBuilders.EventBuilder().setCategory("refresh").setAction("swipe")
					.setLabel("notification").build());
			isRetry = false;
			mSwipeRefreshLayout.setRefreshing(true);
			getNotificationData(true);
		}
	}

	private void setUpPullRefresh() {
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeColors(Utils.getSwipeRefreshColors(activity));
	}

	@Override
	public void onBottomReached() {
		if (!isLoadingPosts && !isRetry) {
			mTracker.send(
					new HitBuilders.EventBuilder().setCategory("load more").setAction("scroll")
							.build());
			getNotificationData(false);
		}
	}

	private void getNotificationData(final boolean firstTime) {
		mSwipeRefreshLayout.setEnabled(false);

		if (firstTime) {
			mPage = 0;
			mList.clear();
		}
		mPage++;
		isLoadingPosts = true;
		mAdapter.notifyDataSetChanged();

		Helper.getNotification(activity, mPage, new NotificationCallback() {

			@Override
			public void onSuccess(List<NotificationModel> modelList) {
				super.onSuccess(modelList);

				if (!isAdded()) {
					return;
				}

				isLoadingPosts = false;
				mList.addAll(modelList);
				mAdapter.notifyDataSetChanged();
				mSwipeRefreshLayout.setEnabled(true);
				mSwipeRefreshLayout.setRefreshing(false);
			}

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);

				isLoadingPosts = false;
				mPage--;
				isRetry = true;
				mAdapter.notifyDataSetChanged();
				mSwipeRefreshLayout.setEnabled(true);
				mSwipeRefreshLayout.setRefreshing(false);
			}
		});
	}

	public ListView getListView() {
		return mListView;
	}

	public class Adapter extends BaseAdapter
			implements PinnedSectionListView.PinnedSectionListAdapter {

		private LayoutInflater inflater;
		private final int TYPE_NOTIFICATION = 0, TYPE_PROGRESS = 1, TYPE_RETRY = 2;

		public Adapter(Context context) {
			this.inflater =
					(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mList.size() + (isRetry || isLoadingPosts ? 1 : 0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			if (position >= mList.size()) {
				return getString(R.string.loading);
			} else {
				return mList.get(position);
			}
		}

		@Override
		public int getViewTypeCount() {
			// TYPE_NOTIFICATION, TYPE_PROGRESS and TYPE_RETRY
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			return position < mList.size() ? TYPE_NOTIFICATION :
					isRetry ? TYPE_RETRY : TYPE_PROGRESS;
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return getItemViewType(position) != TYPE_PROGRESS;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			RetryViewHolder retryHolder = null;
			int item_type = getItemViewType(position);
			if (convertView == null) {
				if (item_type == TYPE_NOTIFICATION) {
					holder = new ViewHolder();
					convertView = inflater.inflate(R.layout.list_notification, parent, false);
					holder.textView_author =
							(TextView) convertView.findViewById(R.id.textView_author);
					holder.textView_date = (TextView) convertView.findViewById(R.id.textView_date);
					holder.textView_content =
							(TextView) convertView.findViewById(R.id.textView_content);
					convertView.setTag(holder);
				} else if (item_type == TYPE_PROGRESS) {
					convertView = inflater.inflate(R.layout.list_progresswheel, parent, false);
				} else {
					retryHolder = new RetryViewHolder();
					convertView = inflater.inflate(R.layout.list_text, parent, false);
					retryHolder.textView = (TextView) convertView.findViewById(R.id.textView);
					convertView.setTag(retryHolder);
				}
			} else {
				if (item_type == TYPE_NOTIFICATION) {
					holder = (ViewHolder) convertView.getTag();
				} else {
					retryHolder = (RetryViewHolder) convertView.getTag();
				}

			}

			if (item_type == TYPE_NOTIFICATION) {
				holder.textView_author.setText(mList.get(position).author);
				holder.textView_date.setText(mList.get(position).date);
				holder.textView_content.setText(mList.get(position).content);
			} else if (item_type == TYPE_RETRY) {
				retryHolder.textView.setText(R.string.click_to_retry);
			}
			return convertView;
		}

		class ViewHolder {
			TextView textView_author;
			TextView textView_date;
			TextView textView_content;
		}

		class RetryViewHolder {
			TextView textView;
		}
	}
}