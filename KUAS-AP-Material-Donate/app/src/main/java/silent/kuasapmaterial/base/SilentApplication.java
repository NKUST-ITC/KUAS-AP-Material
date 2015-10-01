package silent.kuasapmaterial.base;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class SilentApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		initImageLoader(getApplicationContext());
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config =
				new ImageLoaderConfiguration.Builder(context).threadPoolSize(5).build();

		ImageLoader.getInstance().init(config);
	}

	public SilentApplication() {
		super();
	}

	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
	}
}