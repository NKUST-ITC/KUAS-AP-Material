package silent.kuasapmaterial;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.kuas.ap.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.libs.Utils;

public class SubmitNewsActivity extends SilentActivity
		implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

	TextInputLayout mTitleTextInputLayout, mUrlTextInputLayout;
	EditText mTitleEditText, mUrlEditText;
	View mUploadPicView, mSetStartTimeView, mSetEndTimeView;
	TextView mUploadPicTextView, mSetStartTimeTextView, mSetEndTimeTextView;
	ImageView mUploadPicImageView, mSetStartTimeImageView, mSetEndTimeImageView;

	MenuItem mSubmitMenuItem, mPreviewMenuItem;

	private boolean isRecreation = false;
	private boolean isPreviewing = false;

	String mStartDate, mEndDate;

	public static final int START = 0;
	public static final int END = 1;
	public static final String START_DIALOG = "StartDatePickerDialog";
	public static final String END_DIALOG = "EndDatePickerDialog";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_news);
		setUpToolBar(getString(R.string.submit_news));

		initGA("Submit News Screen");
		restoreStates(savedInstanceState);
		findViews();
		setUpViews();
	}

	public void restoreStates(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			isRecreation = true;
			isPreviewing = savedInstanceState.getBoolean("isPreviewing");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean("isPreviewing", isPreviewing);
	}

	private void findViews() {
		mTitleEditText = (EditText) findViewById(R.id.editText_title);
		mUrlEditText = (EditText) findViewById(R.id.editText_url);
		mTitleTextInputLayout = (TextInputLayout) mTitleEditText.getParent();
		mUrlTextInputLayout = (TextInputLayout) mUrlEditText.getParent();

		mUploadPicView = findViewById(R.id.view_upload);
		mSetStartTimeView = findViewById(R.id.view_start_time);
		mSetEndTimeView = findViewById(R.id.view_end_time);

		mUploadPicView = findViewById(R.id.view_upload);
		mSetStartTimeView = findViewById(R.id.view_start_time);
		mSetEndTimeView = findViewById(R.id.view_end_time);

		mUploadPicTextView = (TextView) findViewById(R.id.textView_upload);
		mSetStartTimeTextView = (TextView) findViewById(R.id.textView_start_time);
		mSetEndTimeTextView = (TextView) findViewById(R.id.textView_end_time);

		mUploadPicImageView = (ImageView) findViewById(R.id.imageView_upload);
		mSetStartTimeImageView = (ImageView) findViewById(R.id.imageView_start_time);
		mSetEndTimeImageView = (ImageView) findViewById(R.id.imageView_end_time);
	}

	private void setUpViews() {
		mUploadPicView.setOnClickListener(this);
		mSetStartTimeView.setOnClickListener(this);
		mSetEndTimeView.setOnClickListener(this);

		mTitleTextInputLayout.setHint(getString(R.string.news_title));
		mUrlTextInputLayout.setHint(getString(R.string.news_url));

		Bitmap uploadBitmap = Utils.convertDrawableToBitmap(
				ContextCompat.getDrawable(this, R.drawable.ic_cloud_upload_white_24dp));
		Bitmap sourceBitmap = Utils.convertDrawableToBitmap(
				ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_right_white_24dp));
		int color = ContextCompat.getColor(this, R.color.accent);
		mUploadPicImageView.setImageBitmap(Utils.changeImageColor(uploadBitmap, color));
		mSetStartTimeImageView.setImageBitmap(Utils.changeImageColor(sourceBitmap, color));
		mSetEndTimeImageView.setImageBitmap(Utils.changeImageColor(sourceBitmap, color));
	}

	@Retention(RetentionPolicy.CLASS) @IntDef({START, END}) public @interface DialogType {
	}

	private void showDatePickerDialog(@DialogType int type) {
		Calendar now = Calendar.getInstance();
		DatePickerDialog dpd = DatePickerDialog
				.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
						now.get(Calendar.DAY_OF_MONTH));
		if (type == START) {
			//dpd.setMinDate();
		} else {
			//dpd.setMinDate();
		}
		dpd.setThemeDark(false);
		dpd.vibrate(false);
		dpd.dismissOnPause(false);
		dpd.show(getFragmentManager(), type == START ? START_DIALOG : END_DIALOG);
	}

	private void setUpPreview() {
		if (isPreviewing) {
			mPreviewMenuItem.setIcon(R.drawable.ic_mode_edit_white_24dp);
			mPreviewMenuItem.setTitle(R.string.edit);
		} else {
			mPreviewMenuItem.setIcon(R.drawable.ic_visibility_white_24dp);
			mPreviewMenuItem.setTitle(R.string.preview);
		}
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.submit_news, menu);

		mSubmitMenuItem = menu.findItem(R.id.action_submit);
		mPreviewMenuItem = menu.findItem(R.id.action_preview);

		setUpPreview();

		if (isRecreation) {
			//checkToEnableSend();
		} else {
			//loadMemory();
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_submit:
				return true;
			case R.id.action_preview:
				isPreviewing = !isPreviewing;
				setUpPreview();
				return true;
			case android.R.id.home:
				onBackPressed();
				return true;

			default:
				return false;
		}
	}

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		mStartDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
		mTracker.send(new HitBuilders.EventBuilder().setCategory("date set").setAction("click")
				.setLabel(mEndDate).build());
	}

	@Override
	public void onResume() {
		super.onResume();
		DatePickerDialog sdpd =
				(DatePickerDialog) getFragmentManager().findFragmentByTag(START_DIALOG);
		DatePickerDialog edpd =
				(DatePickerDialog) getFragmentManager().findFragmentByTag(END_DIALOG);

		if (sdpd != null) {
			sdpd.setOnDateSetListener(this);
		}
		if (edpd != null) {
			edpd.setOnDateSetListener(this);
		}
	}
}
