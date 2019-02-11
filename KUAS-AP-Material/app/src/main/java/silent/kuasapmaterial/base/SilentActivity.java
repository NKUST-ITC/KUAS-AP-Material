package silent.kuasapmaterial.base;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kuas.ap.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import silent.kuasapmaterial.AboutActivity;
import silent.kuasapmaterial.BusActivity;
import silent.kuasapmaterial.CourseActivity;
import silent.kuasapmaterial.LeaveActivity;
import silent.kuasapmaterial.LoginActivity;
import silent.kuasapmaterial.MessagesActivity;
import silent.kuasapmaterial.ScoreActivity;
import silent.kuasapmaterial.SettingsActivity;
import silent.kuasapmaterial.UserInfoActivity;
import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.callback.UserInfoCallback;
import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.libs.Memory;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.models.UserInfoModel;

public class SilentActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	public Toolbar toolbar;
	public DrawerLayout drawer;
	public NavigationView navigationView;
	public View headerView;
	public MenuItem selectedMenuItem;

	public int mLayoutID;
	public int mSelectedItem;

	public AnimationActionBarDrawerToggle mDrawerToggle;

	public Tracker mTracker;

	public boolean isDisplayHomeAsUp = false;
	public List<Integer> itemList = new ArrayList<>(
			Arrays.asList(R.id.nav_course, R.id.nav_score, R.id.nav_leave, R.id.nav_bus,
					R.id.nav_simcourse, R.id.nav_messages, R.id.nav_about, R.id.nav_settings));

	public void init(int title, int layout) {
		init(title, layout, -1);
	}

	public void init(int title, int layout, int selectItem) {
		init(getString(title), layout, itemList.indexOf(selectItem));
	}

	public void init(String title, int layout, int selectItem) {
		mLayoutID = layout;
		setUpToolBar(title);
		setUpMenuDrawer(selectItem);
		setDisplayHomeAsUp(false);

		setUpUserPhoto();
		setUpUserInfo();
	}

	public void initGA(String screenName) {
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		mTracker = analytics.newTracker(Constant.GA_ID);
		mTracker.setScreenName(screenName);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	public void setUpToolBar(String title) {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle(title);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}
	}

	public void setUpUserInfo() {
		if (!Memory.getBoolean(this, Constant.PREF_IS_LOGIN, false) || navigationView == null) {
			return;
		}
		String userName = Memory.getString(this, Constant.PREF_USER_NAME, "");
		String userID = Memory.getString(this, Constant.PREF_USER_ID, "");
		if (userName.length() > 0 && userID.length() > 0) {
			((TextView) headerView.findViewById(R.id.textView_name)).setText(userName);
			((TextView) headerView.findViewById(R.id.textView_schoolID)).setText(userID);
		} else {
			Helper.getUserInfo(this, new UserInfoCallback() {

				@Override
				public void onSuccess(UserInfoModel userInfoModel) {
					super.onSuccess(userInfoModel);

					Memory.setString(SilentActivity.this, Constant.PREF_USER_NAME,
							userInfoModel.student_name_cht);
					Memory.setString(SilentActivity.this, Constant.PREF_USER_ID,
							userInfoModel.student_id);
					((TextView) headerView.findViewById(R.id.textView_name))
							.setText(userInfoModel.student_name_cht);
					((TextView) headerView.findViewById(R.id.textView_schoolID))
							.setText(userInfoModel.student_id);
				}
			});
		}
	}

	public void setUpUserPhoto() {
		if (!Memory.getBoolean(this, Constant.PREF_IS_LOGIN, false) || navigationView == null) {
			return;
		}
		boolean isSetUpHeadPhoto = Memory.getBoolean(this, Constant.PREF_HEAD_PHOTO, true);
		try {
			if (isSetUpHeadPhoto) {
				String photo = Memory.getString(this, Constant.PREF_USER_PIC, "");
				if (!TextUtils.isEmpty(photo)) {
					ImageLoader.getInstance().displayImage(photo,
							(ImageView) headerView.findViewById(R.id.imageView_user),
							Utils.getHeadDisplayImageOptions(this,
									getResources().getDimensionPixelSize(R.dimen.head_mycard) / 2));
				} else {
					Helper.getUserPicture(this, new GeneralCallback() {

						@Override
						public void onSuccess(String data) {
							super.onSuccess(data);
							Memory.setString(SilentActivity.this, Constant.PREF_USER_PIC, data);
							ImageLoader.getInstance().displayImage(data,
									(ImageView) headerView.findViewById(R.id.imageView_user),
									Utils.getHeadDisplayImageOptions(SilentActivity.this,
											getResources()
													.getDimensionPixelSize(R.dimen.head_mycard) /
													2));
						}
					});
				}
			} else {
				((ImageView) headerView.findViewById(R.id.imageView_user))
						.setImageResource(R.drawable.ic_account_circle_white_48dp);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public void showUserInfo() {
		startActivity(new Intent(this, UserInfoActivity.class));
	}

	public void setUpMenuDrawer(int selectItem) {
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		navigationView = (NavigationView) findViewById(R.id.nav_view);
		headerView = navigationView.getHeaderView(0);
		if (headerView.findViewById(R.id.layout_user) != null) {
			final boolean isLogin = Memory.getBoolean(this, Constant.PREF_IS_LOGIN, false);
			headerView.findViewById(R.id.layout_user)
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							drawer.closeDrawers();
							if (mLayoutID == R.layout.activity_messages ||
									mLayoutID == R.layout.activity_about) {
								if (isLogin) {
									showUserInfo();
								} else {
									startActivity(
											new Intent(SilentActivity.this, LoginActivity.class));
								}
							} else if (mLayoutID == R.layout.activity_login) {
								Toast.makeText(SilentActivity.this, R.string.login_first,
										Toast.LENGTH_SHORT).show();
							} else {
								showUserInfo();
							}
						}
					});
		}

		drawer.setDrawerShadow(R.drawable.shadow_right, GravityCompat.START);
		drawer.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.main_theme_dark));

		mDrawerToggle = new AnimationActionBarDrawerToggle(this, drawer, R.string.open_drawer,
				R.string.close_drawer) {

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				if (drawerView == navigationView) {
					super.onDrawerSlide(drawerView, slideOffset);
					InputMethodManager inputMethodManager =
							(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
				}
			}

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View drawerView) {
				if (drawerView == navigationView) {
					super.onDrawerClosed(drawerView);
					InputMethodManager inputMethodManager =
							(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
				}
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				if (drawerView == navigationView) {
					super.onDrawerOpened(drawerView);
					InputMethodManager inputMethodManager =
							(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
				}
			}
		};

		// Set the drawer toggle as the DrawerListener
		drawer.setDrawerListener(mDrawerToggle);
		navigationView.setNavigationItemSelectedListener(this);

		mSelectedItem = -1;
		if (-1 < selectItem && selectItem < navigationView.getMenu().size()) {
			selectedMenuItem = navigationView.getMenu().getItem(selectItem);
			selectedMenuItem.setChecked(true);
			mSelectedItem = selectItem;
		}
	}

	public void setDisplayHomeAsUp(boolean value) {
		if (value == isDisplayHomeAsUp) {
			return;
		} else {
			isDisplayHomeAsUp = value;
		}

		ValueAnimator anim;
		if (value) {
			anim = ValueAnimator.ofFloat(0f, 1f);
		} else {
			anim = ValueAnimator.ofFloat(1f, 0f);
		}
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				float slideOffset = (float) valueAnimator.getAnimatedValue();
				setDrawerIconState(slideOffset);
			}
		});
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.setDuration(300);
		anim.start();
	}

	public void setDrawerIconState(float slideOffset) {
		mDrawerToggle.onAnimationDrawerSlide(navigationView, slideOffset);
		InputMethodManager inputMethodManager =
				(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(navigationView.getWindowToken(), 0);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (drawer.isDrawerOpen(navigationView)) {
					drawer.closeDrawer(navigationView);
				} else if (!drawer.isDrawerOpen(navigationView)) {
					drawer.openDrawer(navigationView);
				}
				return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (navigationView == null) {
			return;
		}
		for (int i = 0; i < navigationView.getMenu().size() && mSelectedItem == -1; i++) {
			navigationView.getMenu().getItem(i).setChecked(false);
		}
		setUpUserPhoto();
		checkNetwork();
	}

	public void checkNetwork() {
		if (!Utils.isNetworkConnected(this)) {
			Snackbar.make(findViewById(android.R.id.content), R.string.no_internet,
					Snackbar.LENGTH_INDEFINITE)
					.setAction(R.string.setting_internet, new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
						}
					}).setActionTextColor(ContextCompat.getColor(this, R.color.accent)).show();
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem menuItem) {
		drawer.closeDrawers();
		if (menuItem == selectedMenuItem) {
			return false;
		}
		boolean isLogin = Memory.getBoolean(this, Constant.PREF_IS_LOGIN, false);
		if (isLogin) {
			if (menuItem.getItemId() == R.id.nav_messages) {
				startActivity(new Intent(this, MessagesActivity.class));
			} else if (menuItem.getItemId() == R.id.nav_bus) {
				if (Memory.getBoolean(SilentActivity.this, Constant.PREF_BUS_ENABLE, true)) {
					startActivity(new Intent(SilentActivity.this, BusActivity.class));
				} else {
					Toast.makeText(SilentActivity.this, R.string.can_not_use_bus,
							Toast.LENGTH_SHORT).show();
					return false;
				}
			} else if (menuItem.getItemId() == R.id.nav_course) {
				startActivity(new Intent(this, CourseActivity.class));
			} else if (menuItem.getItemId() == R.id.nav_about) {
				startActivity(new Intent(this, AboutActivity.class));
			} else if (menuItem.getItemId() == R.id.nav_score) {
				startActivity(new Intent(this, ScoreActivity.class));
			} else if (menuItem.getItemId() == R.id.nav_leave) {
				startActivity(new Intent(this, LeaveActivity.class));
			} else if (menuItem.getItemId() == R.id.nav_settings) {
				startActivity(new Intent(this, SettingsActivity.class));
			}
			if (mLayoutID != R.layout.activity_logout && mLayoutID != R.layout.activity_login) {
				finish();
			}
		} else {
			if (menuItem.getItemId() == R.id.nav_messages) {
				startActivity(new Intent(this, MessagesActivity.class));
				if (mLayoutID != R.layout.activity_login) {
					finish();
				}
			} else if (menuItem.getItemId() == R.id.nav_about) {
				startActivity(new Intent(this, AboutActivity.class));
				if (mLayoutID != R.layout.activity_login) {
					finish();
				}
			} else {
				Toast.makeText(this, R.string.login_first, Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		if (drawer != null && navigationView != null && drawer.isDrawerOpen(navigationView)) {
			drawer.closeDrawers();
		} else {
			if (mLayoutID == R.layout.activity_logout) {
				if (mTracker != null) {
					mTracker.send(new HitBuilders.EventBuilder().setCategory("logout dialog")
							.setAction("create").build());
				}
				new AlertDialog.Builder(this).setTitle(R.string.app_name)
						.setMessage(R.string.logout_check).setPositiveButton(R.string.determine,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (mTracker != null) {
									mTracker.send(new HitBuilders.EventBuilder()
											.setCategory("logout dialog").setAction("click")
											.build());
								}
								clearUserData();
								Memory.setBoolean(SilentActivity.this, Constant.PREF_AUTO_LOGIN,
										false);
								finish();
							}
						}).setNegativeButton(R.string.cancel, null).show();
			} else {
				super.onBackPressed();
			}
		}
	}

	public void clearUserData() {
		Memory.setBoolean(this, Constant.PREF_IS_LOGIN, false);
		Memory.setString(this, Constant.PREF_USER_PIC, "");
		Memory.setString(this, Constant.PREF_USER_ID, "");
		Memory.setString(this, Constant.PREF_USER_NAME, "");
	}

	public class AnimationActionBarDrawerToggle extends ActionBarDrawerToggle {

		public AnimationActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout,
		                                      int openDrawerContentDescRes,
		                                      int closeDrawerContentDescRes) {
			super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
		}

		public void onAnimationDrawerSlide(View drawerView, float slideOffset) {
			super.onDrawerSlide(drawerView, slideOffset);
		}
	}
}