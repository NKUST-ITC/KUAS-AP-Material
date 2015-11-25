package silent.kuasapmaterial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kuas.ap.R;

import java.util.ArrayList;
import java.util.List;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.callback.ScoreCallback;
import silent.kuasapmaterial.callback.SemesterCallback;
import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.libs.MaterialProgressBar;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.models.ScoreDetailModel;
import silent.kuasapmaterial.models.ScoreModel;
import silent.kuasapmaterial.models.SemesterModel;

public class ScoreActivity extends SilentActivity implements SwipeRefreshLayout.OnRefreshListener {

	View mPickYmsView;
	ImageView mPickYmsImageView;
	TextView mNoScoreTextView, mPickYmsTextView;
	LinearLayout mNoScoreLinearLayout;
	MaterialProgressBar mMaterialProgressBar;
	SwipeRefreshLayout mSwipeRefreshLayout;
	ScrollView mScrollView;
	TableLayout mScoreTableLayout, mDetailTableLayout;

	String mYms;
	List<ScoreModel> mList;
	List<SemesterModel> mSemesterList;
	SemesterModel mSelectedModel;
	ScoreDetailModel mScoreDetailModel;
	private int mPos = 0;
	boolean isRetry = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		setContentView(R.layout.activity_score);
		init(R.string.score, R.layout.activity_score, R.id.nav_score);

		initGA("Score Screen");
		restoreArgs(savedInstanceState);
		findViews();
		setUpViews();
	}

	@Override
	public void finish() {
		super.finish();

		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	private void restoreArgs(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mYms = savedInstanceState.getString("mYms");
			mPos = savedInstanceState.getInt("mPos");
			isRetry = savedInstanceState.getBoolean("isRetry");

			if (savedInstanceState.containsKey("mList")) {
				mList = new Gson().fromJson(savedInstanceState.getString("mList"),
						new TypeToken<List<ScoreModel>>() {
						}.getType());
			}
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
			if (savedInstanceState.containsKey("mScoreDetailModel")) {
				mScoreDetailModel = new Gson()
						.fromJson(savedInstanceState.getString("mScoreDetailModel"),
								new TypeToken<ScoreDetailModel>() {
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

		outState.putString("mYms", mYms);
		outState.putBoolean("isRetry", isRetry);
		if (mScrollView != null) {
			outState.putInt("mPos", mScrollView.getVerticalScrollbarPosition());
		}
		if (mList != null) {
			outState.putString("mList", new Gson().toJson(mList));
		}
		if (mSelectedModel != null) {
			outState.putString("mSelectedModel", new Gson().toJson(mSelectedModel));
		}
		if (mSemesterList != null) {
			outState.putString("mSemesterList", new Gson().toJson(mSemesterList));
		}
		if (mScoreDetailModel != null) {
			outState.putString("mScoreDetailModel", new Gson().toJson(mScoreDetailModel));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case Constant.REQUEST_PICK_SEMESTER:
				if (resultCode == RESULT_OK && data != null) {
					if (data.hasExtra("mSelectedModel")) {
						mSelectedModel = new Gson().fromJson(data.getStringExtra("mSelectedModel"),
								new TypeToken<SemesterModel>() {
								}.getType());
						mYms = mSelectedModel.value;
						mPickYmsTextView.setText(mSelectedModel.text);
						getData();
					}
				}
				break;
		}
	}

	private void getSemester() {
		Helper.getSemester(this, new SemesterCallback() {

			@Override
			public void onSuccess(List<SemesterModel> modelList, SemesterModel selectedModel) {
				super.onSuccess(modelList, selectedModel);
				mSemesterList = modelList;
				mSelectedModel = selectedModel;
				mYms = mSelectedModel.value;
				mPickYmsTextView.setText(mSelectedModel.text);
				getData();
			}

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);
				isRetry = true;
				setUpScoreTable();
			}
		});
	}

	private void findViews() {
		mScrollView = (ScrollView) findViewById(R.id.scrollView);
		mPickYmsTextView = (TextView) findViewById(R.id.textView_pickYms);
		mPickYmsView = findViewById(R.id.view_pickYms);
		mPickYmsImageView = (ImageView) findViewById(R.id.imageView_pickYms);
		mMaterialProgressBar = (MaterialProgressBar) findViewById(R.id.materialProgressBar);
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
		mNoScoreLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_no_course);
		mNoScoreTextView = (TextView) findViewById(R.id.textView_no_course);
		mScoreTableLayout = (TableLayout) findViewById(R.id.tableLayout_score);
		mDetailTableLayout = (TableLayout) findViewById(R.id.tableLayout_detail);
	}

	private void setUpViews() {
		setUpPullRefresh();

		Bitmap sourceBitmap = Utils.convertDrawableToBitmap(
				ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_down_white_24dp));
		int color = ContextCompat.getColor(this, R.color.accent);
		mPickYmsImageView.setImageBitmap(Utils.changeImageColor(sourceBitmap, color));

		mScrollView.scrollTo(0, mPos);
		mPickYmsView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectedModel == null) {
					return;
				}
				mTracker.send(
						new HitBuilders.EventBuilder().setCategory("pick yms").setAction("click")
								.build());
				Intent intent = new Intent(ScoreActivity.this, PickSemesterActivity.class);
				intent.putExtra("mSemesterList", new Gson().toJson(mSemesterList));
				intent.putExtra("mSelectedModel", new Gson().toJson(mSelectedModel));
				startActivityForResult(intent, Constant.REQUEST_PICK_SEMESTER);
			}
		});
		mNoScoreLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRetry) {
					mTracker.send(
							new HitBuilders.EventBuilder().setCategory("retry").setAction("click")
									.setLabel((mSemesterList == null) + "").build());
					isRetry = false;
					if (mSemesterList == null || mSelectedModel == null) {
						getSemester();
					} else {
						getData();
					}
				} else {
					if (mSemesterList == null || mSelectedModel == null) {
						getSemester();
						return;
					}
					mTracker.send(new HitBuilders.EventBuilder().setCategory("pick yms")
							.setAction("click").build());
					Intent intent = new Intent(ScoreActivity.this, PickSemesterActivity.class);
					intent.putExtra("mSemesterList", new Gson().toJson(mSemesterList));
					intent.putExtra("mSelectedModel", new Gson().toJson(mSelectedModel));
					startActivityForResult(intent, Constant.REQUEST_PICK_SEMESTER);
				}
			}
		});

		if (mSelectedModel != null && mSemesterList != null) {
			mPickYmsTextView.setText(mSelectedModel.text);
			setUpScoreTable();
		} else {
			mPickYmsView.setEnabled(false);
			getSemester();
		}
	}

	@Override
	public void onRefresh() {
		if (mYms != null) {
			mTracker.send(new HitBuilders.EventBuilder().setCategory("refresh").setAction("swipe")
					.build());
			isRetry = false;
			mSwipeRefreshLayout.setRefreshing(true);
			getData();
		}
	}

	private void setUpPullRefresh() {
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeColors(Utils.getSwipeRefreshColors(this));
	}

	private void getData() {
		if (!mSwipeRefreshLayout.isRefreshing()) {
			mMaterialProgressBar.setVisibility(View.VISIBLE);
		}
		mPickYmsView.setEnabled(false);
		mScrollView.setVisibility(View.GONE);
		mNoScoreLinearLayout.setVisibility(View.GONE);
		mSwipeRefreshLayout.setEnabled(false);

		Helper.getScoreTimeTable(this, mYms.split(",")[0], mYms.split(",")[1], new ScoreCallback() {

			@Override
			public void onSuccess(List<ScoreModel> modelList, ScoreDetailModel scoreDetailModel) {
				super.onSuccess(modelList, scoreDetailModel);
				mList = modelList;
				mScoreDetailModel = scoreDetailModel;
				setUpScoreTable();
			}

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);

				mList.clear();
				isRetry = true;
				setUpScoreTable();
			}

			@Override
			public void onTokenExpired() {
				super.onTokenExpired();
				Utils.createTokenExpired(ScoreActivity.this).show();
				mTracker.send(
						new HitBuilders.EventBuilder().setCategory("token").setAction("expired")
								.build());
			}
		});
	}

	private void setUpScoreTable() {
		mScoreTableLayout.setStretchAllColumns(true);
		mScoreTableLayout.removeAllViews();
		mDetailTableLayout.removeAllViews();

		if (mList.size() == 0) {
			if (isRetry) {
				mNoScoreTextView.setText(R.string.click_to_retry);
			} else {
				mNoScoreTextView.setText(getString(R.string.score_no_score, "\uD83D\uDE0B"));
			}
			mMaterialProgressBar.setVisibility(View.GONE);
			mSwipeRefreshLayout.setEnabled(true);
			mSwipeRefreshLayout.setRefreshing(false);
			mScrollView.setVisibility(View.VISIBLE);
			mNoScoreLinearLayout.setVisibility(View.VISIBLE);
			mPickYmsView.setEnabled(true);
			return;
		}

		TableRow sectionTableRow = new TableRow(this);
		String[] sections = getResources().getStringArray(R.array.score_sections);
		for (int i = 0; i < sections.length; i++) {
			TextView sectionTextView = new TextView(this);
			sectionTextView.setText(sections[i]);
			sectionTextView.setTextColor(ContextCompat.getColor(this, R.color.accent));
			sectionTextView.setTextSize(15);
			sectionTextView.setGravity(Gravity.CENTER);

			int drawable = getResources()
					.getIdentifier("table_top_" + (i == 0 ? "left" : (i == 1 ? "center" : "right")),
							"drawable", getPackageName());
			sectionTextView.setBackgroundResource(drawable);

			sectionTableRow.addView(sectionTextView);
		}
		mScoreTableLayout.addView(sectionTableRow);

		for (int i = 0; i < mList.size(); i++) {
			TableRow scoreTableRow = new TableRow(this);
			for (int j = 0; j < sections.length; j++) {
				TextView scoreTextView = new TextView(this);
				scoreTextView.setTextSize(14);
				scoreTextView.setTextColor(ContextCompat.getColor(this, R.color.black_text));
				scoreTextView.setText(j == 0 ? mList.get(i).title :
						(j == 1 ? mList.get(i).middle_score : mList.get(i).final_score));
				scoreTextView.setGravity(Gravity.CENTER);

				int drawable = getResources()
						.getIdentifier("table_" + (i == mList.size() - 1 ? "bottom_" : "normal_") +
										(j == 0 ? "left" : (j == 1 ? "center" : "right")),
								"drawable", getPackageName());
				scoreTextView.setBackgroundResource(drawable);

				scoreTableRow.addView(scoreTextView,
						new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
								TableRow.LayoutParams.MATCH_PARENT));
			}
			mScoreTableLayout.addView(scoreTableRow);
		}

		List<String> detailList = new ArrayList<>();
		String[] detailSections = getResources().getStringArray(R.array.score_detail_sections);
		detailList.add(Double.toString(mScoreDetailModel.conduct));
		detailList.add(Double.toString(mScoreDetailModel.average));
		detailList.add(mScoreDetailModel.class_rank);
		detailList.add(Double.toString(mScoreDetailModel.class_percentage));

		for (int i = 0; i < detailList.size(); i++) {
			TableRow detailTableRow = new TableRow(this);
			TextView detailTextView = new TextView(this);
			detailTextView.setTextSize(14);
			detailTextView.setTextColor(ContextCompat.getColor(this, R.color.black_text));
			detailTextView.setGravity(Gravity.CENTER);
			boolean isDetailHaveContent =
					!(detailList.get(i).equals("0.0") || detailList.get(i).length() == 0);
			detailTextView
					.setText(detailSections[i] + (isDetailHaveContent ? detailList.get(i) : "N/A"));

			int drawable = getResources().getIdentifier("table_oneitem_" +
							(i == 0 ? "top" : (i == detailList.size() - 1 ? "bottom" : "normal")),
					"drawable", getPackageName());
			detailTextView.setBackgroundResource(drawable);

			detailTableRow.addView(detailTextView,
					new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
							TableRow.LayoutParams.MATCH_PARENT));
			mDetailTableLayout.addView(detailTableRow);
		}

		mMaterialProgressBar.setVisibility(View.GONE);
		mSwipeRefreshLayout.setEnabled(true);
		mSwipeRefreshLayout.setRefreshing(false);
		mScrollView.setVisibility(View.VISIBLE);
		mNoScoreLinearLayout.setVisibility(View.GONE);
		mPickYmsView.setEnabled(true);
	}
}
