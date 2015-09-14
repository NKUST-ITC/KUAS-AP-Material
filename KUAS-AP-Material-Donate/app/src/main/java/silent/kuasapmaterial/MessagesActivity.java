package silent.kuasapmaterial;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.kuas.ap.donate.R;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.fragment.NotificationFragment;
import silent.kuasapmaterial.fragment.PhoneFragment;
import silent.kuasapmaterial.fragment.ScheduleFragment;

public class MessagesActivity extends SilentActivity {

	TabLayout mTabLayout;
	ViewPager mViewPager;

	MessagesPagerAdapter mMessagesPagerAdapter;
	private boolean blockReselection = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		setContentView(R.layout.activity_messages);
		init(R.string.messages, R.layout.activity_messages, R.id.nav_messages);

		findViews();
		setUpViews();
	}

	@Override
	public void finish() {
		super.finish();

		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
				} else {
					blockReselection = false;
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
				if (!blockReselection) {
					Fragment fragment =
							getSupportFragmentManager().getFragments().get(tab.getPosition());
					if (fragment instanceof NotificationFragment) {
						((NotificationFragment) fragment).getListView().smoothScrollToPosition(0);
					} else if (fragment instanceof PhoneFragment) {
						((PhoneFragment) fragment).getListView().smoothScrollToPosition(0);
					} else if (fragment instanceof ScheduleFragment) {
						((ScheduleFragment) fragment).getListView().smoothScrollToPosition(0);
					}
				} else {
					blockReselection = false;
				}
			}
		});
		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset,
			                           int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				blockReselection = true;
			}

			@Override
			public void onPageScrollStateChanged(int state) {
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
