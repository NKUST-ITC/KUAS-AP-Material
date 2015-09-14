package silent.kuasapmaterial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kuas.ap.donate.R;

import silent.kuasapmaterial.libs.MetaballView;

public class LoadingActivity extends AppCompatActivity {

	private MetaballView metaballView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		findViews();
		setUpViews();
	}

	private void findViews() {
		metaballView = (MetaballView) findViewById(R.id.metaball);
	}

	private void setUpViews() {
		metaballView.setPaintMode(1);
	}
}
