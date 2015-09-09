package silent.kuasapmaterial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;

import java.util.Random;

import silent.kuasapmaterial.base.SilentActivity;

public class AboutActivity extends SilentActivity
		implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

	CollapsingToolbarLayout mCollapsingToolbar;
	View view_fb, view_github, view_email, view_itc, view_easter_egg;

	private long lastDebugPressTime = 0l;
	private int easterEggCount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		setContentView(R.layout.activity_about);
		init(R.string.about, this, R.id.nav_about);

		findViews();
		setUpViews();
	}

	@Override
	public void finish() {
		super.finish();

		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
		}
		return true;
	}

	private void findViews() {
		mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

		view_fb = findViewById(R.id.view_fb);
		view_github = findViewById(R.id.view_github);
		view_email = findViewById(R.id.view_email);
		view_itc = findViewById(R.id.view_itc);
		view_easter_egg = findViewById(R.id.view_easter_egg);
	}

	private void setUpViews() {
		mCollapsingToolbar.setTitle(getString(R.string.about));
		mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));

		view_fb.setOnClickListener(this);
		view_github.setOnClickListener(this);
		view_email.setOnClickListener(this);
		view_itc.setOnClickListener(this);
		view_easter_egg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.view_fb || v.getId() == R.id.view_itc) {
			try {
				Intent browserIntent =
						new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/735951703168873"));
				startActivity(browserIntent);
			} catch (Exception e) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("https://www.facebook.com/KUASITC"));
				startActivity(browserIntent);
			}
		} else if (v.getId() == R.id.view_github) {
			Intent browserIntent =
					new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kuastw"));
			startActivity(browserIntent);
		} else if (v.getId() == R.id.view_email) {
			Intent browserIntent =
					new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:1102108133@kuas.edu.tw"));
			startActivity(browserIntent);
		} else if (v.getId() == R.id.view_easter_egg) {
			if (System.currentTimeMillis() - lastDebugPressTime <= 500) {
				easterEggCount++;
				if (easterEggCount == 3) {
					lastDebugPressTime = 0l;
					easterEggCount = 0;
					String[] easterEggList = getResources().getStringArray(R.array.easter_egg);
					Random random = new Random();
					Snackbar.make(findViewById(android.R.id.content),
							easterEggList[random.nextInt(easterEggList.length)],
							Snackbar.LENGTH_SHORT)
							.setAction(R.string.ok, new View.OnClickListener() {
								@Override
								public void onClick(View v) {

								}
							}).setActionTextColor(getResources().getColor(R.color.accent)).show();
				}
			} else {
				easterEggCount = 1;
			}
			lastDebugPressTime = System.currentTimeMillis();
		}
	}
}
