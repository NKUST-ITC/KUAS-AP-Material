package silent.kuasapmaterial.base;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.kuas.ap.BuildConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import io.fabric.sdk.android.Fabric;

public class SilentApplication extends Application {

	public SilentApplication() {
		super();
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config =
				new ImageLoaderConfiguration.Builder(context).threadPoolSize(5).build();

		ImageLoader.getInstance().init(config);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Fabric.with(this, new Crashlytics.Builder()
				.core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());

		initImageLoader(getApplicationContext());
	}

	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
	}
}