package silent.kuasapmaterial.base;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import silent.kuasapmaterial.R;
import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Utils;

public class SilentActivity extends AppCompatActivity {

	public Toolbar toolbar;
	public DrawerLayout drawer;
	public NavigationView navigationView;

	public AnimationActionBarDrawerToggle mDrawerToggle;

	public boolean isDisplayHomeAsUp = false;
	public List<Integer> itemList = new ArrayList<>(
			Arrays.asList(R.id.nav_course, R.id.nav_score, R.id.nav_leave, R.id.nav_bus,
					R.id.nav_simcourse, R.id.nav_messages, R.id.nav_about, R.id.nav_settings));

	public void init(int title, NavigationView.OnNavigationItemSelectedListener listener) {
		init(title, listener, -1);
	}

	public void init(int title, NavigationView.OnNavigationItemSelectedListener listener,
	                 int selectItem) {
		init(getString(title), listener, itemList.indexOf(selectItem));
	}

	public void init(String title, NavigationView.OnNavigationItemSelectedListener listener,
	                 int selectItem) {
		setUpToolBar(title);
		setUpMenuDrawer(listener, selectItem);
		setDisplayHomeAsUp(false);
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

	public void setUpMenuDrawer(NavigationView.OnNavigationItemSelectedListener listener,
	                            int selectItem) {
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		navigationView = (NavigationView) findViewById(R.id.nav_view);
		if (navigationView.findViewById(R.id.layout_user) != null) {
			navigationView.findViewById(R.id.layout_user)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							drawer.closeDrawers();
							Log.d(Constant.TAG, "cccc");
						}
					});
		}

		drawer.setDrawerShadow(R.drawable.shadow_right, GravityCompat.START);
		drawer.setStatusBarBackgroundColor(getResources().getColor(R.color.main_theme_dark));

		mDrawerToggle = new AnimationActionBarDrawerToggle(this, drawer, R.string.open_drawer,
				R.string.close_drawer) {

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				if (drawerView == navigationView) {
					super.onDrawerSlide(drawerView, slideOffset);
				}
			}

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View drawerView) {
				if (drawerView == navigationView) {
					super.onDrawerClosed(drawerView);
				}
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				if (drawerView == navigationView) {
					super.onDrawerOpened(drawerView);
				}
			}
		};

		// Set the drawer toggle as the DrawerListener
		drawer.setDrawerListener(mDrawerToggle);
		navigationView.setNavigationItemSelectedListener(listener);

		if (-1 < selectItem && selectItem < navigationView.getMenu().size()) {
			navigationView.getMenu().getItem(selectItem).setChecked(true);
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
					return true;
				} else if (!drawer.isDrawerOpen(navigationView)) {
					drawer.openDrawer(navigationView);
					return true;
				}
				break;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
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
					}).setActionTextColor(getResources().getColor(R.color.accent)).show();
		}
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