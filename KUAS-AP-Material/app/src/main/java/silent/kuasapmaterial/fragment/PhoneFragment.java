package silent.kuasapmaterial.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import silent.kuasapmaterial.base.SilentFragment;
import silent.kuasapmaterial.libs.PinnedSectionListView;
import silent.kuasapmaterial.models.PhoneModel;

public class PhoneFragment extends SilentFragment implements AdapterView.OnItemClickListener {

	private View view;
	private PinnedSectionListView mListView;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	List<PhoneModel> mList;
	Activity activity;

	private int mInitListPos = 0, mInitListOffset = 0;

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
		mSwipeRefreshLayout.setEnabled(false);

		mList = new ArrayList<>();
		mList.add(new PhoneModel("高雄應用科技大學總機", "(07) 381-4526"));
		mList.add(new PhoneModel("建工校安專線", "0916-507-506"));
		mList.add(new PhoneModel("燕巢校安專線", "0925-350-995"));
		mList.add(new PhoneModel("事務組", "(07) 381-4526 #2650"));
		mList.add(new PhoneModel("營繕組", "(07) 381-4526 #2630"));
		mList.add(new PhoneModel("課外活動組", "(07) 381-4526 #2525"));
		mList.add(new PhoneModel("諮商輔導中心", "(07) 381-4526 #2541"));
		mList.add(new PhoneModel("圖書館", "(07) 381-4526 #3100"));
		mList.add(new PhoneModel("建工校外賃居服務中心", "(07) 381-4526 #3420"));
		mList.add(new PhoneModel("燕巢校外賃居服務中心", "(07) 381-4526 #8615"));
		PhoneAdapter adapter = new PhoneAdapter(activity);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);

		((ListView) mListView).setSelectionFromTop(mInitListPos, mInitListOffset);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		mTracker.send(new HitBuilders.EventBuilder().setCategory("call phone").setAction("create")
				.build());
		new AlertDialog.Builder(activity).setTitle(R.string.call_phone_title)
				.setMessage(getString(R.string.call_phone_content, mList.get(position).name))
				.setPositiveButton(R.string.call_phone, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mTracker.send(new HitBuilders.EventBuilder().setCategory("call phone")
								.setAction("click").build());
						Intent myIntentDial = new Intent(Intent.ACTION_DIAL,
								Uri.parse("tel:" + mList.get(position).phone.replace("#", ",")));
						startActivity(myIntentDial);
					}
				}).setNegativeButton(R.string.cancel, null).show();
	}

	public ListView getListView() {
		return mListView;
	}

	public class PhoneAdapter extends BaseAdapter
			implements PinnedSectionListView.PinnedSectionListAdapter {
		private LayoutInflater inflater;

		public PhoneAdapter(Context context) {
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
		public PhoneModel getItem(int position) {
			return mList.get(position);
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return true;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.list_phone, parent, false);
				holder.textView_name = (TextView) convertView.findViewById(R.id.textView_name);
				holder.textView_phone = (TextView) convertView.findViewById(R.id.textView_phone);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.textView_name.setText(mList.get(position).name);
			holder.textView_phone.setText(mList.get(position).phone);
			return convertView;
		}

		class ViewHolder {
			TextView textView_name;
			TextView textView_phone;
		}
	}
}