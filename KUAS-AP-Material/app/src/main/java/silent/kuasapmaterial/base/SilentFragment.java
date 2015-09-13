package silent.kuasapmaterial.base;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import silent.kuasapmaterial.libs.Constant;

public class SilentFragment extends Fragment {

	public Tracker mTracker;

	public void initGA(String screenName, Activity activity) {
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(activity);
		mTracker = analytics.newTracker(Constant.GA_ID);
		mTracker.setScreenName(screenName);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}
}