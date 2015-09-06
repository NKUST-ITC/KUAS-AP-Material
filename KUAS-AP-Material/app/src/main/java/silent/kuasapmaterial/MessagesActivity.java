package silent.kuasapmaterial;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.fragment.NotificationFragment;
import silent.kuasapmaterial.fragment.PhoneFragment;
import silent.kuasapmaterial.fragment.ScheduleFragment;

public class MessagesActivity extends SilentActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	TabLayout mTabLayout;
	ViewPager mViewPager;

	MessagesPagerAdapter mMessagesPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		init(R.string.messages, this, R.id.nav_messages);

		findViews();
		setUpViews();
	}

	// TODO Wait for handle navigation items
	@Override
	public boolean onNavigationItemSelected(MenuItem menuItem) {
		drawer.closeDrawers();
		if (menuItem.isChecked()) {
			return true;
		}
		return true;
	}

	private void findViews() {
		mTabLayout = (TabLayout) findViewById(R.id.tabs_main);
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
	}

	private void setUpViews() {
		mMessagesPagerAdapter = new MessagesPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mMessagesPagerAdapter);
		mViewPager.setOffscreenPageLimit(3);

		mTabLayout.setTabsFromPagerAdapter(mMessagesPagerAdapter);
		mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				int position = tab.getPosition();
				if (mViewPager.getCurrentItem() != position) {
					mViewPager.setCurrentItem(position);
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
			}
		});
		mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
	}

	private class MessagesPagerAdapter extends FragmentPagerAdapter {

		public MessagesPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return new NotificationFragment();
			} else if (position == 1) {
				return new PhoneFragment();
			} else {
				return new ScheduleFragment();
			}
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getResources().getStringArray(R.array.messages)[position];
		}
	}
}
