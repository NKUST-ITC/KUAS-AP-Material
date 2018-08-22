package silent.kuasapmaterial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kuas.ap.R;

import java.util.ArrayList;
import java.util.List;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.callback.CourseCallback;
import silent.kuasapmaterial.callback.SemesterCallback;
import silent.kuasapmaterial.libs.AlarmHelper;
import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.libs.MaterialProgressBar;
import silent.kuasapmaterial.libs.Memory;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.models.CourseModel;
import silent.kuasapmaterial.models.SemesterModel;

public class CourseActivity extends SilentActivity implements SwipeRefreshLayout.OnRefreshListener {

	View mPickYmsView;
	ImageView mPickYmsImageView;
	TextView mNoCourseTextView, mHolidayTextView, mPickYmsTextView;
	LinearLayout mNoCourseLinearLayout;
	MaterialProgressBar mMaterialProgressBar;
	SwipeRefreshLayout mSwipeRefreshLayout;
	RecyclerView mRecyclerView;

	CourseGridAdapter mAdapter;

	String mYms;
	List<String> mSections;
	List<List<CourseModel>> mList;
	List<SemesterModel> mSemesterList;
	SemesterModel mSelectedModel;
	boolean isRetry = false;
	private int mPos = 0;
	private boolean isHoliday, isNight, isHolidayNight, isB, isHolidayB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		setContentView(R.layout.activity_course);
		init(R.string.course, R.layout.activity_course, R.id.nav_course);

		initGA("Course Screen");
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
			isHoliday = savedInstanceState.getBoolean("isHoliday");
			isNight = savedInstanceState.getBoolean("isNight");
			isHolidayNight = savedInstanceState.getBoolean("isHolidayNight");
			isB = savedInstanceState.getBoolean("isB");
			isHolidayB = savedInstanceState.getBoolean("isHolidayB");
			isRetry = savedInstanceState.getBoolean("isRetry");

			if (savedInstanceState.containsKey("mSections")) {
				mSections = new Gson().fromJson(savedInstanceState.getString("mSections"),
						new TypeToken<List<String>>() {

						}.getType());
			}
			if (savedInstanceState.containsKey("mList")) {
				mList = new Gson().fromJson(savedInstanceState.getString("mList"),
						new TypeToken<List<List<CourseModel>>>() {

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
		}

		if (mList == null) {
			mList = new ArrayList<>();
		}
		if (mSections == null) {
			mSections = new ArrayList<>();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString("mYms", mYms);
		outState.putBoolean("isHoliday", isHoliday);
		outState.putBoolean("isNight", isNight);
		outState.putBoolean("isHolidayNight", isHolidayNight);
		outState.putBoolean("isB", isB);
		outState.putBoolean("isHolidayB", isHolidayB);
		outState.putBoolean("isRetry", isRetry);
		if (mRecyclerView != null) {
			outState.putInt("mPos", mRecyclerView.getVerticalScrollbarPosition());
		}
		if (mSections != null) {
			outState.putString("mSections", new Gson().toJson(mSections));
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
						getData(false);
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
				getData(true);
			}

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);
				isRetry = true;
				setUpCourseTable();
			}
		});
	}

	private void findViews() {
		mRecyclerView = findViewById(R.id.recyclerView);
		mPickYmsTextView = findViewById(R.id.textView_pickYms);
		mPickYmsView = findViewById(R.id.view_pickYms);
		mPickYmsImageView = findViewById(R.id.imageView_pickYms);
		mMaterialProgressBar = findViewById(R.id.materialProgressBar);
		mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
		mNoCourseLinearLayout = findViewById(R.id.linearLayout_no_course);
		mNoCourseTextView = findViewById(R.id.textView_no_course);
		mHolidayTextView = findViewById(R.id.textView_holiday);
	}

	private void setUpViews() {
		setUpPullRefresh();
		mHolidayTextView.setText(getString(R.string.course_holiday, "\uD83D\uDE06"));

		Bitmap sourceBitmap = Utils.convertDrawableToBitmap(
				ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_down_white_24dp));
		int color = ContextCompat.getColor(this, R.color.accent);
		mPickYmsImageView.setImageBitmap(Utils.changeImageColor(sourceBitmap, color));

		mRecyclerView.scrollTo(0, mPos);
		mPickYmsView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSelectedModel == null) {
					return;
				}
				mTracker.send(
						new HitBuilders.EventBuilder().setCategory("pick yms").setAction("click")
								.build());
				Intent intent = new Intent(CourseActivity.this, PickSemesterActivity.class);
				intent.putExtra("mSemesterList", new Gson().toJson(mSemesterList));
				intent.putExtra("mSelectedModel", new Gson().toJson(mSelectedModel));
				startActivityForResult(intent, Constant.REQUEST_PICK_SEMESTER);
			}
		});
		mNoCourseLinearLayout.setOnClickListener(new View.OnClickListener() {

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
						getData(false);
					}
				} else {
					if (mSemesterList == null || mSelectedModel == null) {
						getSemester();
						return;
					}
					mTracker.send(new HitBuilders.EventBuilder().setCategory("pick yms")
							.setAction("click").build());
					Intent intent = new Intent(CourseActivity.this, PickSemesterActivity.class);
					intent.putExtra("mSemesterList", new Gson().toJson(mSemesterList));
					intent.putExtra("mSelectedModel", new Gson().toJson(mSelectedModel));
					startActivityForResult(intent, Constant.REQUEST_PICK_SEMESTER);
				}
			}
		});

		if (mSelectedModel != null && mSemesterList != null) {
			mPickYmsTextView.setText(mSelectedModel.text);
			setUpCourseTable();
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
			getData(false);
		}
	}

	private void setUpPullRefresh() {
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeColors(Utils.getSwipeRefreshColors(this));
	}

	private void getData(final boolean isSave) {
		if (!mSwipeRefreshLayout.isRefreshing()) {
			mMaterialProgressBar.setVisibility(View.VISIBLE);
		}
		mPickYmsView.setEnabled(false);
		mRecyclerView.setVisibility(View.GONE);
		mNoCourseLinearLayout.setVisibility(View.GONE);
		mHolidayTextView.setVisibility(View.GONE);
		mSwipeRefreshLayout.setEnabled(false);

		Helper.getCourseTimeTable(this, mYms.split(",")[0], mYms.split(",")[1],
				new CourseCallback() {

					@Override
					public void onSuccess(List<String> sections,
					                      List<List<CourseModel>> modelList) {
						super.onSuccess(sections, modelList);

						if (isSave &&
								Memory.getBoolean(CourseActivity.this, Constant.PREF_COURSE_NOTIFY,
										false)) {
							AlarmHelper.setCourseNotification(CourseActivity.this, modelList);
						}

						mSections = sections;
						mList = modelList;
						setUpCourseTable();
						mPickYmsView.setEnabled(true);
					}

					@Override
					public void onFail(String errorMessage) {
						super.onFail(errorMessage);

						mList.clear();
						isRetry = true;
						setUpCourseTable();
						mPickYmsView.setEnabled(true);
					}

					@Override
					public void onTokenExpired() {
						super.onTokenExpired();
						Utils.showTokenExpired(CourseActivity.this);
						mTracker.send(new HitBuilders.EventBuilder().setCategory("token")
								.setAction("expired").build());
					}
				});
	}

	private void setUpCourseTable() {
		if (mList.size() == 0) {
			if (isRetry) {
				mNoCourseTextView.setText(R.string.click_to_retry);
			} else {
				mNoCourseTextView.setText(getString(R.string.course_no_course, "\uD83D\uDE0B"));
			}
			mMaterialProgressBar.setVisibility(View.GONE);
			mSwipeRefreshLayout.setEnabled(true);
			mSwipeRefreshLayout.setRefreshing(false);
			mNoCourseLinearLayout.setVisibility(View.VISIBLE);
			mRecyclerView.setVisibility(View.INVISIBLE);
			mHolidayTextView.setVisibility(View.GONE);
			return;
		} else {
			mNoCourseLinearLayout.setVisibility(View.GONE);
		}
		checkCourseTableType();

		mMaterialProgressBar.setVisibility(View.GONE);
		mSwipeRefreshLayout.setEnabled(true);
		mSwipeRefreshLayout.setRefreshing(false);

		mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), getWidth(),
				LinearLayoutManager.VERTICAL, false));
		mRecyclerView.setHasFixedSize(true);
		if (mAdapter == null) {
			mAdapter = new CourseGridAdapter();
			mRecyclerView.setAdapter(new CourseGridAdapter());
		} else {
			mAdapter.notifyDataSetChanged();
		}
		mRecyclerView.setVisibility(View.VISIBLE);
	}

	private void showCourseDialog(final int weekday, final int section) {
		mTracker.send(new HitBuilders.EventBuilder().setCategory("show course").setAction("click")
				.setLabel(mList.get(weekday).get(section).title).build());

		StringBuilder instructors = new StringBuilder(
				mList.get(weekday).get(section).instructors.size() > 0 ?
						mList.get(weekday).get(section).instructors.get(0) : "");
		for (int k = 1; k < mList.get(weekday).get(section).instructors.size(); k++) {
			instructors.append(",");
			instructors.append(mList.get(weekday).get(section).instructors.get(k));
		}

		CourseModel courseModel = mList.get(weekday).get(section);
		new AlertDialog.Builder(CourseActivity.this).setTitle(R.string.course_dialog_title)
				.setMessage(
						getString(R.string.course_dialog_messages, courseModel.title, instructors,
								courseModel.room,
								courseModel.start_time + " - " + courseModel.end_time))
				.setPositiveButton(R.string.ok, null).show();
	}

	private void checkCourseTableType() {
		isHoliday = false;
		isNight = false;
		isHolidayNight = false;
		isB = false;
		isHolidayB = false;

		for (int i = 0;
		     i < mList.size() && !(isHolidayNight && isHoliday && isNight && isHolidayB && isB);
		     i++) {
			if (mList.get(i) != null) {
				if (i > 4) {
					isHoliday = true;
				}
				for (int j = 0; j < mList.get(i).size() &&
						!(isHolidayNight && isHoliday && isNight && isHolidayB && isB); j++) {
					if (mList.get(i).get(j) != null) {
						if (j > 10) {
							if (i > 4) {
								isHolidayNight = true;
							} else {
								isNight = true;
							}
						} else if (j == 10) {
							if (i > 4) {
								isHolidayB = true;
							} else {
								isB = true;
							}
						}
					}
				}
			}
		}

		if (!((Utils.isWide(this) || Utils.isLand(this)) && isHoliday)) {
			if (isHoliday) {
				mHolidayTextView.setVisibility(View.VISIBLE);
			} else {
				mHolidayTextView.setVisibility(View.GONE);
			}
		}
	}

	private int getWidth() {
		return (Utils.isWide(this) || Utils.isLand(this)) && isHoliday ? 8 : 6;
	}

	private int getHeight() {
		return (isNight || ((Utils.isWide(this) || Utils.isLand(this)) && isHolidayNight)) ? 16 :
				(isB || ((Utils.isWide(this) || Utils.isLand(this)) && isHolidayB)) ? 12 : 11;
	}

	public class CourseGridAdapter extends RecyclerView.Adapter<CourseGridAdapter.CourseViewHolder>
			implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int position = (int) v.getTag();
			showCourseDialog(position % getWidth() - 1, position / getWidth() - 1);
		}

		@NonNull
		@Override
		public CourseGridAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
		                                                             int viewType) {
			View view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.grid_course, parent, false);
			return new CourseViewHolder(view);
		}

		@Override
		public void onBindViewHolder(@NonNull CourseGridAdapter.CourseViewHolder holder,
		                             int position) {
			if (position < getWidth() || position % getWidth() == 0) {
				holder.textView
						.setTextColor(ContextCompat.getColor(CourseActivity.this, R.color.accent));
				holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

				if (position % getWidth() == 0) {
					if (position / getWidth() > 0) {
						holder.textView.setText(mSections.get(position / getWidth() - 1));
					} else {
						holder.textView.setText("");
					}
				} else {
					holder.textView
							.setText(getResources().getStringArray(R.array.weekdays)[position % 7]);
				}

				if (position < getWidth()) {
					if (position == 0) {
						holder.textView.setBackgroundResource(R.drawable.course_top_left_normal);
					} else if (position == getWidth() - 1) {
						holder.textView.setBackgroundResource(R.drawable.course_top_right_normal);
					} else {
						holder.textView.setBackgroundResource(R.drawable.course_top_center_normal);
					}
				} else {
					if (position / getWidth() == getHeight() - 1) {
						holder.textView.setBackgroundResource(R.drawable.course_bottom_left_normal);
					} else {
						holder.textView.setBackgroundResource(R.drawable.course_normal_left_normal);
					}
				}
			} else {
				holder.textView.setTextColor(
						ContextCompat.getColor(CourseActivity.this, R.color.black_text));
				holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

				List<CourseModel> modelList = position % getWidth() - 1 < mList.size() ?
						mList.get(position % getWidth() - 1) : null;
				if (modelList != null) {
					CourseModel model = position / getWidth() - 1 < modelList.size() ?
							modelList.get(position / getWidth() - 1) : null;
					if (model != null) {
						holder.textView.setText(model.title.substring(0, 2));
						holder.itemView.setTag(position);
						holder.itemView.setOnClickListener(this);
					} else {
						holder.textView.setText("");
					}
				} else {
					holder.textView.setText("");
				}

				if (position / getWidth() == getHeight() - 1) {
					if (position == getItemCount() - 1) {
						holder.textView
								.setBackgroundResource(R.drawable.course_bottom_right_normal);
					} else {
						holder.textView
								.setBackgroundResource(R.drawable.course_bottom_center_normal);
					}
				} else {
					if (position % getWidth() == getWidth() - 1) {
						holder.textView
								.setBackgroundResource(R.drawable.course_normal_right_normal);
					} else {
						holder.textView
								.setBackgroundResource(R.drawable.course_normal_center_normal);
					}
				}
			}
		}

		@Override
		public void onViewDetachedFromWindow(@NonNull CourseGridAdapter.CourseViewHolder holder) {
			super.onViewDetachedFromWindow(holder);
			// StackOverflow : http://goo.gl/hWm6CI
			// The problem is that there are animations running when RecyclerView
			// is trying to reuse the view.
			holder.itemView.clearAnimation();
		}

		@Override
		public int getItemCount() {
			return getWidth() * getHeight();
		}

		public class CourseViewHolder extends RecyclerView.ViewHolder {

			public final TextView textView;

			CourseViewHolder(View view) {
				super(view);
				textView = view.findViewById(R.id.textView);
			}
		}
	}
}
