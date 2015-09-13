package silent.kuasapmaterial;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kuas.ap.R;

import java.util.List;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.models.SemesterModel;
import com.kuas.ap.R;

public class PickSemesterActivity extends SilentActivity
		implements AdapterView.OnItemClickListener {

	ListView mListView;

	List<SemesterModel> mSemesterList;
	SemesterModel mSelectedModel;
	private int mInitListPos = 0, mInitListOffset = 0;
	SemesterAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.push_up_in, R.anim.hold);
		setContentView(R.layout.activity_pick_semester);
		setUpToolBar(getString(R.string.pick_semester));

		restoreArgs(savedInstanceState);
		getBundle();
		findViews();
		setUpViews();
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

	private void getBundle() {
		Bundle mBundle = getIntent().getExtras();
		if (mBundle != null) {
			mSelectedModel = new Gson()
					.fromJson(mBundle.getString("mSelectedModel"), new TypeToken<SemesterModel>() {
					}.getType());
			mSemesterList = new Gson().fromJson(mBundle.getString("mSemesterList"),
					new TypeToken<List<SemesterModel>>() {
					}.getType());
		} else {
			Toast.makeText(this, R.string.something_error, Toast.LENGTH_SHORT).show();
		}
	}

	private void restoreArgs(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mInitListPos = savedInstanceState.getInt("mInitListPos");
			mInitListOffset = savedInstanceState.getInt("mInitListOffset");

			if (savedInstanceState.containsKey("mSelectedModel")) {
				mSelectedModel = new Gson().fromJson(savedInstanceState.getString("mSelectedModel"),
						new TypeToken<SemesterModel>() {
						}.getType());
			}
			if (savedInstanceState.containsKey("mSemesterList")) {
				mSemesterList = new Gson().fromJson(savedInstanceState.getString("mSemesterList"),
						new TypeToken<List<SemesterModel>>() {
						}.getType());
			}
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
		outState.putString("mSelectedModel", new Gson().toJson(mSelectedModel));
		outState.putString("mSemesterList", new Gson().toJson(mSemesterList));
	}

	private void findViews() {
		mListView = (ListView) findViewById(R.id.listView);
	}

	private void setUpViews() {
		mListView.setOnItemClickListener(this);
		mAdapter = new SemesterAdapter(this);
		mListView.setAdapter(mAdapter);

		mListView.setSelectionFromTop(mInitListPos, mInitListOffset);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		Intent intent = new Intent();
		intent.putExtra("mSelectedModel", new Gson().toJson(mSemesterList.get(position)));
		setResult(RESULT_OK, intent);
		finish();
	}

	public class SemesterAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public SemesterAdapter(Context context) {
			this.inflater =
					(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mSemesterList.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public SemesterModel getItem(int position) {
			return mSemesterList.get(position);
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
				convertView = inflater.inflate(R.layout.list_semester, parent, false);
				holder.textView = (TextView) convertView.findViewById(R.id.textView);
				holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (mSemesterList.get(position).text.equals(mSelectedModel.text)) {
				Bitmap sourceBitmap = Utils.convertDrawableToBitmap(
						getResources().getDrawable(R.drawable.ic_done_white_24dp));
				int color = getResources().getColor(R.color.accent);
				holder.imageView.setImageBitmap(Utils.changeImageColor(sourceBitmap, color));
				holder.imageView.setVisibility(View.VISIBLE);
				holder.textView.setTextColor(getResources().getColor(R.color.accent));
				holder.textView.setText(mSemesterList.get(position).text);
			} else {
				holder.imageView.setVisibility(View.GONE);
				holder.textView.setTextColor(getResources().getColor(R.color.black_text));
				holder.textView.setText(mSemesterList.get(position).text);
			}

			return convertView;
		}

		class ViewHolder {
			TextView textView;
			ImageView imageView;
		}
	}
}
