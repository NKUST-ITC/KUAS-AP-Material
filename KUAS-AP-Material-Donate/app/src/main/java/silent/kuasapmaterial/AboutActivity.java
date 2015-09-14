package silent.kuasapmaterial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.kuas.ap.donate.R;

import java.util.Random;

import silent.kuasapmaterial.base.SilentActivity;

public class AboutActivity extends SilentActivity implements View.OnClickListener {

	CollapsingToolbarLayout mCollapsingToolbar;
	View view_fb, view_github, view_email, view_itc, view_easter_egg;
	FloatingActionButton mFab;

	private long lastDebugPressTime = 0l;
	private int easterEggCount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		setContentView(R.layout.activity_about);
		init(R.string.about, R.layout.activity_about, R.id.nav_about);

		initGA("About Screen");
		findViews();
		setUpViews();
	}

	@Override
	public void finish() {
		super.finish();

		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	private void findViews() {
		mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

		view_fb = findViewById(R.id.view_fb);
		view_github = findViewById(R.id.view_github);
		view_email = findViewById(R.id.view_email);
		view_itc = findViewById(R.id.view_itc);
		view_easter_egg = findViewById(R.id.view_easter_egg);

		mFab = (FloatingActionButton) findViewById(R.id.fab);
	}

	private void setUpViews() {
		mCollapsingToolbar.setTitle(getString(R.string.about));
		mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));

		view_fb.setOnClickListener(this);
		view_github.setOnClickListener(this);
		view_email.setOnClickListener(this);
		view_itc.setOnClickListener(this);
		view_easter_egg.setOnClickListener(this);
		mFab.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.view_fb || v.getId() == R.id.view_itc) {
			mTracker.send(
					new HitBuilders.EventBuilder().setCategory("fb").setAction("click").build());
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
			mTracker.send(new HitBuilders.EventBuilder().setCategory("github").setAction("click")
					.build());
			Intent browserIntent =
					new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kuastw"));
			startActivity(browserIntent);
		} else if (v.getId() == R.id.view_email) {
			mTracker.send(
					new HitBuilders.EventBuilder().setCategory("email").setAction("click").build());
			Intent browserIntent =
					new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:1102108133@kuas.edu.tw"));
			startActivity(browserIntent);
		} else if (v.getId() == R.id.view_easter_egg) {
			mTracker.send(
					new HitBuilders.EventBuilder().setCategory("easter egg").setAction("click")
							.build());
			if (System.currentTimeMillis() - lastDebugPressTime <= 500) {
				easterEggCount++;
				if (easterEggCount == 3) {
					mTracker.send(new HitBuilders.EventBuilder().setCategory("easter egg")
							.setAction("click").setLabel("success").build());
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
		} else if (v.getId() == R.id.fab) {
			mTracker.send(
					new HitBuilders.EventBuilder().setCategory("open source").setAction("click")
							.build());
			startActivity(new Intent(this, OpenSourceActivity.class));
		}
	}
}
