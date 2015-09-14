package silent.kuasapmaterial;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kuas.ap.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.callback.UserInfoCallback;
import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.libs.Memory;
import silent.kuasapmaterial.libs.OverScrollView;
import silent.kuasapmaterial.libs.ProgressWheel;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.models.UserInfoModel;

public class UserInfoActivity extends SilentActivity {

	ProgressWheel mProgressWheel;
	View mDetailView;
	TextView mUserTextView, mEducationSystemTextView, mDepartmentTextView, mClassTextView,
			mStuIdTextView;
	ImageView mPhotoImageView;
	OverScrollView mScrollView;

	UserInfoModel mUserInfoModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		setUpToolBar(getString(R.string.user));

		initGA("User Info Screen");
		restoreArgs(savedInstanceState);
		findViews();
		setUpViews();
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
			if (savedInstanceState.containsKey("mUserInfoModel")) {
				mUserInfoModel = new Gson().fromJson(savedInstanceState.getString("mUserInfoModel"),
						new TypeToken<UserInfoModel>() {
						}.getType());
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mUserInfoModel != null) {
			outState.putString("mUserInfoModel", new Gson().toJson(mUserInfoModel));
		}
	}

	private void findViews() {
		mProgressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
		mDetailView = findViewById(R.id.layout_detail);
		mPhotoImageView = (ImageView) findViewById(R.id.imageView_photo);
		mScrollView = (OverScrollView) findViewById(R.id.scrollView);

		mUserTextView = (TextView) findViewById(R.id.textView_student_name_content);
		mEducationSystemTextView = (TextView) findViewById(R.id.textView_education_system_content);
		mDepartmentTextView = (TextView) findViewById(R.id.textView_department_content);
		mClassTextView = (TextView) findViewById(R.id.textView_student_class_content);
		mStuIdTextView = (TextView) findViewById(R.id.textView_student_id_content);
	}

	private void setUpViews() {
		ViewGroup.LayoutParams params = mPhotoImageView.getLayoutParams();
		params.height = (int) (Utils.getDisplayHeight(this) * 0.5);
		mPhotoImageView.setLayoutParams(params);

		mScrollView.setOnOverScrolledListener(new OverScrollView.OnOverScrolledListener() {

			@Override
			public void onOverScrolled(ScrollView scrollView, int deltaX, int deltaY,
			                           boolean clampedX, boolean clampedY) {
				if (deltaY < 0) {
					ViewGroup.LayoutParams params = mPhotoImageView.getLayoutParams();
					params.height -= deltaY;
					mPhotoImageView.setLayoutParams(params);
				}
			}
		});
		mScrollView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					ViewGroup.LayoutParams params = mPhotoImageView.getLayoutParams();
					params.height = (int) (Utils.getDisplayHeight(UserInfoActivity.this) * 0.5);
					mPhotoImageView.setLayoutParams(params);
				}
				return false;
			}
		});
		if (mUserInfoModel != null) {
			setUpUser();
		} else {
			getData();
		}
	}

	private void getData() {
		mProgressWheel.setVisibility(View.VISIBLE);

		Helper.getUserInfo(this, new UserInfoCallback() {
			@Override
			public void onSuccess(UserInfoModel userInfoModel) {
				super.onSuccess(userInfoModel);

				mUserInfoModel = userInfoModel;
				setUpUser();
			}
		});
	}

	private void setUpUser() {
		mEducationSystemTextView.setText(mUserInfoModel.education_system);
		mStuIdTextView.setText(mUserInfoModel.student_id);
		mDepartmentTextView.setText(mUserInfoModel.department);
		mClassTextView.setText(mUserInfoModel.student_class);
		mUserTextView.setText(mUserInfoModel.student_name_cht);

		String photo = Memory.getString(this, Constant.PREF_USER_PIC, "");
		if (mImageLoader == null) {
			mImageLoader = Utils.getDefaultImageLoader(this);
		}
		if (photo.length() > 0) {
			setUpUserPhoto(photo);
		} else {
			Helper.getUserPicture(this, new GeneralCallback() {

				@Override
				public void onSuccess(String data) {
					super.onSuccess(data);
					setUpUserPhoto(data);
				}

				@Override
				public void onFail(String errorMessage) {
					super.onFail(errorMessage);
					Toast.makeText(UserInfoActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onTokenExpired() {
					super.onTokenExpired();
					Utils.createTokenExpired(UserInfoActivity.this).show();
				}
			});
		}
	}

	private void setUpUserPhoto(String photo) {
		mImageLoader.displayImage(photo, mPhotoImageView, Utils.getDefaultDisplayImageOptions(),
				new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						mDetailView.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						mProgressWheel.setVisibility(View.GONE);
						mPhotoImageView.setVisibility(View.VISIBLE);
						mDetailView.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}
				});
	}
}
