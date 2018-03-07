package silent.kuasapmaterial;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.kuas.ap.R;

import java.util.ArrayList;
import java.util.List;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.callback.NewsCallback;
import silent.kuasapmaterial.fragment.NewsFragment;
import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.libs.Memory;
import silent.kuasapmaterial.libs.NewsPagerTransformer;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.models.NewsModel;

import static com.kuas.ap.R.string.news;

public class LogoutActivity extends SilentActivity {

	TextView mTitleTextView, mPositionTextView, mTotalTextView;

	ViewPager viewPager;
	List<NewsFragment> fragments = new ArrayList<>();

	NewsPagerTransformer transformer;

	BottomNavigationView navigation;

	List<NewsModel> newsList = new ArrayList<>();

	String mTitle, mContent, mURL;
	Boolean hasNews, isBusSaved;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTitle = Memory.getString(this, Constant.PREF_NEWS_TITLE, "");
		mContent = Memory.getString(this, Constant.PREF_NEWS_CONTENT, "");
		mURL = Memory.getString(this, Constant.PREF_NEWS_URL, "");
		hasNews = mContent.length() > 0;

		if (mContent.length() == 0) {
			setContentView(R.layout.activity_logout);
		} else {
			setContentView(R.layout.activity_logout_news);
		}
		init(news, R.layout.activity_logout);

		initGA("Logout Screen");
		restoreArgs(savedInstanceState);
		setUpBusNotify();
		findViews();
		getNews();
	}

	private void restoreArgs(Bundle savedInstanceState) {
		isBusSaved = savedInstanceState != null && savedInstanceState.getBoolean("isBusSaved");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean("isBusSaved", isBusSaved);
	}

	private void setUpBusNotify() {
		if (!Memory.getBoolean(this, Constant.PREF_BUS_NOTIFY, false) || isBusSaved) {
			return;
		}
		final Dialog progressDialog = Utils.createLoadingDialog(this, R.string.loading);
		progressDialog.show();
		Utils.setUpBusNotify(this, new GeneralCallback() {

			@Override
			public void onTokenExpired() {
				super.onTokenExpired();
				if (isFinishing()) {
					return;
				}
				progressDialog.dismiss();
			}

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);
				if (isFinishing()) {
					return;
				}
				mTracker.send(
						new HitBuilders.EventBuilder().setCategory("notify bus").setAction("status")
								.setLabel("fail " + errorMessage).build());
				progressDialog.dismiss();
			}

			@Override
			public void onSuccess() {
				super.onSuccess();
				isBusSaved = true;
				if (isFinishing()) {
					return;
				}
				mTracker.send(
						new HitBuilders.EventBuilder().setCategory("notify bus").setAction("status")
								.setLabel("success").build());
				progressDialog.dismiss();
			}
		});
	}

	private void findViews() {
		if (hasNews) {
			viewPager = (ViewPager) findViewById(R.id.viewPager_news);
			mTitleTextView = (TextView) findViewById(R.id.textView_title);
			mPositionTextView = (TextView) findViewById(R.id.textView_position);
			mTotalTextView = (TextView) findViewById(R.id.textView_total);
		}
		navigation = (BottomNavigationView) findViewById(R.id.navigation);
	}

	private void setUpViews() {
		transformer = new NewsPagerTransformer(this);
		viewPager.setPageTransformer(false, transformer);
		for (int i = 0; i < newsList.size(); i++) {
			fragments.add(new NewsFragment());
		}
		viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

			@Override
			public Fragment getItem(int position) {
				NewsFragment fragment = fragments.get(position);
				fragment.setData(newsList.get(position));
				return fragment;
			}

			@Override
			public int getCount() {
				return newsList.size();
			}

			@Override
			public Parcelable saveState() {
				return null;
			}
		});
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset,
			                           int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				updateView();
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		viewPager.setOffscreenPageLimit(3);
		updateView();
		navigation.setOnNavigationItemSelectedListener(
				new BottomNavigationView.OnNavigationItemSelectedListener() {

					@Override
					public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
						boolean isLogin =
								Memory.getBoolean(LogoutActivity.this, Constant.PREF_IS_LOGIN,
										false);
						if (isLogin) {
							if (menuItem.getItemId() == R.id.nav_bus) {
								startActivity(new Intent(LogoutActivity.this, BusActivity.class));
							} else if (menuItem.getItemId() == R.id.nav_course) {
								startActivity(
										new Intent(LogoutActivity.this, CourseActivity.class));
							} else if (menuItem.getItemId() == R.id.nav_score) {
								startActivity(new Intent(LogoutActivity.this, ScoreActivity.class));
							}
							if (mLayoutID != R.layout.activity_logout &&
									mLayoutID != R.layout.activity_login) {
								finish();
							}
						} else {
							Toast.makeText(LogoutActivity.this, R.string.login_first,
									Toast.LENGTH_SHORT).show();
							return false;
						}
						return false;
					}
				});
	}

	@SuppressLint("DefaultLocale")
	private void updateView() {
		String format = viewPager.getAdapter().getCount() >= 10 ? "%02d" : "%d";
		mTitleTextView.setText(newsList.get(viewPager.getCurrentItem()).title);
		mPositionTextView.setText(String.format(format, viewPager.getCurrentItem() + 1));
		mTotalTextView.setText(String.format(" / %d", viewPager.getAdapter().getCount()));
	}

	public void getNews() {
		Helper.getNews(this, new NewsCallback() {

			@Override
			public void onSuccess(List<NewsModel> modelList) {
				super.onSuccess(modelList);
				newsList = modelList;
				setUpViews();
			}
		});
	}
}
