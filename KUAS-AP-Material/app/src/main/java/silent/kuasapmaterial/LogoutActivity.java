package silent.kuasapmaterial;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.kuas.ap.R;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.MaterialProgressBar;
import silent.kuasapmaterial.libs.Memory;
import silent.kuasapmaterial.libs.Utils;

public class LogoutActivity extends SilentActivity implements View.OnClickListener {

	TextView mTitleTextView;
	WebView mWebView;
	MaterialProgressBar mMaterialProgressBar;
	Button mLogoutButton, mOpenUrlButton;

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
		init(R.string.news, R.layout.activity_logout);

		initGA("Logout Screen");
		restoreArgs(savedInstanceState);
		setUpBusNotify();
		findViews();
		setUpViews();
	}

	private void restoreArgs(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			isBusSaved = savedInstanceState.getBoolean("isBusSaved");
		} else {
			isBusSaved = false;
		}
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
			mTitleTextView = (TextView) findViewById(R.id.textView_title);
			mWebView = (WebView) findViewById(R.id.webView);
			mMaterialProgressBar = (MaterialProgressBar) findViewById(R.id.materialProgressBar);
			mOpenUrlButton = (Button) findViewById(R.id.button_openUrl);
		}
		mLogoutButton = (Button) findViewById(R.id.button_logout);
	}

	private void setUpViews() {
		if (hasNews) {
			mWebView.setVisibility(View.GONE);
			mMaterialProgressBar.setVisibility(View.VISIBLE);

			mTitleTextView.setText(mTitle);
			mWebView.setBackgroundColor(0);
			mWebView.loadDataWithBaseURL("", mContent, "text/html", "UTF-8", "");
			mWebView.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					if (isFinishing()) {
						return;
					}
					mWebView.setVisibility(View.VISIBLE);
					mMaterialProgressBar.setVisibility(View.GONE);
				}
			});

			if (mURL.length() > 0 && mURL.startsWith("http")) {
				mOpenUrlButton.setOnClickListener(this);
				mOpenUrlButton.setVisibility(View.VISIBLE);
			} else {
				mOpenUrlButton.setVisibility(View.GONE);
			}
		}
		mLogoutButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_openUrl) {
			mTracker.send(new HitBuilders.EventBuilder().setCategory("open url").setAction("click")
					.build());
			String shareData = mTitle + "\n" + mURL +
					"\n\n" + getString(R.string.send_from);
			CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
			Bitmap icon =
					BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_white_24dp);
			builder.setActionButton(icon, getString(R.string.share),
					Utils.createSharePendingIntent(this, shareData));
			builder.setToolbarColor(ContextCompat.getColor(this, R.color.main_theme));
			CustomTabsIntent customTabsIntent = builder.build();
			customTabsIntent.launchUrl(this, Uri.parse(mURL));
		} else if (v.getId() == R.id.button_logout) {
			mTracker.send(new HitBuilders.EventBuilder().setCategory("logout").setAction("click")
					.build());
			clearUserData();
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}
	}
}
