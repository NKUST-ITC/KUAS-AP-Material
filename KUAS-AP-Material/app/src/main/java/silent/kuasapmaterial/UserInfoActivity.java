package silent.kuasapmaterial;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kuas.ap.R;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.callback.UserInfoCallback;
import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.libs.Memory;
import silent.kuasapmaterial.libs.ProgressWheel;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.models.UserInfoModel;

public class UserInfoActivity extends SilentActivity {

	ProgressWheel mProgressWheel;

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
	}

	private void setUpViews() {
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

	// TODO Should Upate Layout
	private void setUpUser() {
		((TextView) findViewById(R.id.education)).setText("學制：" + mUserInfoModel.education_system);
		((TextView) findViewById(R.id.id)).setText("學號：" + mUserInfoModel.student_id);
		((TextView) findViewById(R.id.department)).setText("科系：" + mUserInfoModel.department);
		((TextView) findViewById(R.id.stuClass)).setText("班級：" + mUserInfoModel.student_class);
		((TextView) findViewById(R.id.userName)).setText(mUserInfoModel.student_name_cht);
		String photo = Memory.getString(this, Constant.PREF_USER_PIC, "");
		if (mImageLoader == null) {
			mImageLoader = Utils.getDefaultImageLoader(this);
		}
		if (photo.length() > 0) {
			mImageLoader.displayImage(photo, (ImageView) findViewById(R.id.picture),
					Utils.getDefaultDisplayImageOptions());
			mProgressWheel.setVisibility(View.GONE);
		} else {
			Helper.getUserPicture(this, new GeneralCallback() {

				@Override
				public void onSuccess(String data) {
					super.onSuccess(data);
					mImageLoader.displayImage(data, (ImageView) findViewById(R.id.picture),
							Utils.getDefaultDisplayImageOptions());
					mProgressWheel.setVisibility(View.GONE);
				}

				@Override
				public void onTokenExpired() {
					super.onTokenExpired();
					Utils.createTokenExpired(UserInfoActivity.this).show();
				}
			});
		}
	}
}
