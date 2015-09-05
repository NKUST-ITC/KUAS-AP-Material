package silent.kuasapmaterial;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.callback.ServerStatusCallback;
import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.libs.Memory;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.models.ServerStatusModel;

public class LoginActivity extends AppCompatActivity
		implements TextView.OnEditorActionListener, View.OnClickListener {

	TextInputLayout mIdTextInputLayout, mPasswordTextInputLayout;
	EditText mIdEditText, mPasswordEditText;
	ImageView dot_ap, dot_leave, dot_bus;
	CheckBox mRememberCheckBox;
	TextView mVersionTextView;
	Button mLoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViews();
		setUpViews();
		getVersion();
		checkServerStatus();
		getNews();
	}

	private void getVersion() {
		try {
			PackageInfo pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			mVersionTextView.setText(getString(R.string.version, pkgInfo.versionName));
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			mVersionTextView.setText(getString(R.string.version, "1.0.0"));
		}
		Helper.getAppVersion(this, new GeneralCallback() {
			@Override
			public void onSuccess(String data) {
				super.onSuccess(data);
				mVersionTextView.setText(getString(R.string.version, data));
			}
		});
	}

	private void checkServerStatus() {
		Helper.getServerStatus(this, new ServerStatusCallback() {
			@Override
			public void onSuccess(ServerStatusModel model) {
				super.onSuccess(model);
				dot_ap.setImageResource(
						model.ap_status == 200 ? R.drawable.dot_green : R.drawable.dot_red);
				dot_leave.setImageResource(
						model.leave_status == 200 ? R.drawable.dot_green : R.drawable.dot_red);
				dot_bus.setImageResource(
						model.bus_status == 200 ? R.drawable.dot_green : R.drawable.dot_red);
			}

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);
				dot_ap.setImageResource(R.drawable.dot_red);
				dot_leave.setImageResource(R.drawable.dot_red);
				dot_bus.setImageResource(R.drawable.dot_red);
			}
		});
	}

	private void getNews() {
		Helper.getNews(this);
	}

	private void findViews() {
		mIdEditText = (EditText) findViewById(R.id.editText_id);
		mIdTextInputLayout = (TextInputLayout) mIdEditText.getParent();
		mPasswordEditText = (EditText) findViewById(R.id.editText_password);
		mPasswordTextInputLayout = (TextInputLayout) mPasswordEditText.getParent();

		dot_ap = (ImageView) findViewById(R.id.dot_ap);
		dot_leave = (ImageView) findViewById(R.id.dot_leave);
		dot_bus = (ImageView) findViewById(R.id.dot_bus);

		mVersionTextView = (TextView) findViewById(R.id.textView_version);
		mRememberCheckBox = (CheckBox) findViewById(R.id.checkbox_remember);

		mLoginButton = (Button) findViewById(R.id.button_login);
	}

	private void setUpViews() {
		mLoginButton.setOnClickListener(this);
		mPasswordEditText.setOnEditorActionListener(this);
		mIdTextInputLayout.setHint(getString(R.string.id_hint));
		mPasswordTextInputLayout.setHint(getString(R.string.password_hint));
		mIdEditText.setText(Memory.getString(this, Constant.PREF_USERNAME, ""));
		mPasswordEditText.setText(Memory.getString(this, Constant.PREF_PASSWORD, ""));

		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}

		mRememberCheckBox
				.setChecked(Memory.getBoolean(this, Constant.PREF_REMEMBER_PASSWORD, true));
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_login) {
			login();
		}
	}

	private void login() {
		mIdTextInputLayout.setErrorEnabled(false);
		mPasswordTextInputLayout.setErrorEnabled(false);

		final String id = mIdEditText.getText().toString();
		final String pwd = mPasswordEditText.getText().toString();

		if (id.length() == 0) {
			mIdTextInputLayout.setError(getString(R.string.enter_username_hint));
			mIdTextInputLayout.setErrorEnabled(true);
			return;
		}
		if (pwd.length() == 0) {
			mPasswordTextInputLayout.setError(getString(R.string.enter_password_hint));
			mPasswordTextInputLayout.setErrorEnabled(true);
			return;
		}

		Memory.setBoolean(this, Constant.PREF_REMEMBER_PASSWORD, mRememberCheckBox.isChecked());
		final Dialog progressDialog = Utils.createLoadingDialog(this, R.string.login_ing);
		progressDialog.show();
		Helper.login(this, id, pwd, new GeneralCallback() {
			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);

				progressDialog.dismiss();
				Toast.makeText(LoginActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess() {
				super.onSuccess();

				progressDialog.dismiss();
				Memory.setString(LoginActivity.this, Constant.PREF_USERNAME, id);
				Memory.setString(LoginActivity.this, Constant.PREF_PASSWORD,
						mRememberCheckBox.isChecked() ? pwd : "");
				startActivity(new Intent(LoginActivity.this, LogoutActivity.class));
			}
		});
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v.getId() == R.id.editText_password) {
			login();
			return true;
		}
		return false;
	}
}
