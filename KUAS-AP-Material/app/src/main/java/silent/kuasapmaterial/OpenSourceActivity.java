package silent.kuasapmaterial;

import android.os.Bundle;
import android.view.MenuItem;

import silent.kuasapmaterial.base.SilentActivity;

public class OpenSourceActivity extends SilentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open_source);
		setUpToolBar(getString(R.string.about_open_source_title));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return false;
	}
}
