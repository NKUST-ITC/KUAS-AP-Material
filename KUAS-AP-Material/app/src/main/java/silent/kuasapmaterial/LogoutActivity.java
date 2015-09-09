package silent.kuasapmaterial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Memory;
import silent.kuasapmaterial.libs.ProgressWheel;

public class LogoutActivity extends SilentActivity
		implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

	TextView mTitleTextView;
	WebView mWebView;
	ProgressWheel mProgressWheel;
	Button mLogoutButton, mOpenUrlButton;

	String mTitle, mContent, mURL;
	Boolean hasNews;

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
		init(R.string.news, this);

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
		if (menuItem.getItemId() == R.id.nav_messages) {
			startActivity(new Intent(this, MessagesActivity.class));
		} else if (menuItem.getItemId() == R.id.nav_bus) {
			startActivity(new Intent(this, BusActivity.class));
		} else if (menuItem.getItemId() == R.id.nav_course) {
			startActivity(new Intent(this, CourseActivity.class));
		} else if (menuItem.getItemId() == R.id.nav_about) {
			startActivity(new Intent(this, AboutActivity.class));
		}
		return true;
	}

	private void findViews() {
		if (hasNews) {
			mTitleTextView = (TextView) findViewById(R.id.textView_title);
			mWebView = (WebView) findViewById(R.id.webView);
			mProgressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
			mOpenUrlButton = (Button) findViewById(R.id.button_openUrl);
		}
		mLogoutButton = (Button) findViewById(R.id.button_logout);
	}

	private void setUpViews() {
		if (hasNews) {
			mWebView.setVisibility(View.GONE);
			mProgressWheel.setVisibility(View.VISIBLE);

			mTitleTextView.setText(mTitle);
			mWebView.setBackgroundColor(0);
			mWebView.loadDataWithBaseURL("", mContent, "text/html", "UTF-8", "");
			mWebView.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					mWebView.setVisibility(View.VISIBLE);
					mProgressWheel.setVisibility(View.GONE);
					mProgressWheel.stopSpinning();
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
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mURL));
			startActivity(browserIntent);
		} else if (v.getId() == R.id.button_logout) {
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}
	}
}
