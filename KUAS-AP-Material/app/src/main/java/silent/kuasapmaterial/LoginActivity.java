package silent.kuasapmaterial;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import silent.kuasapmaterial.base.SilentActivity;
import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.callback.ServerStatusCallback;
import silent.kuasapmaterial.libs.Constant;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.libs.Memory;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.models.ServerStatusModel;

public class LoginActivity extends SilentActivity
		implements TextView.OnEditorActionListener, View.OnClickListener,
		NavigationView.OnNavigationItemSelectedListener {

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

		init(R.string.app_name, this);
		findViews();
		setUpViews();
		getVersion();
		checkServerStatus();
		getNews();
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

		String pwd = Memory.getString(this, Constant.PREF_PASSWORD, "");
		if (pwd.length() > 0) {
			try {
				byte[] TextByte = Utils.DecryptAES(Constant.IvAES.getBytes("UTF-8"),
						Constant.KeyAES.getBytes("UTF-8"),
						Base64.decode(pwd.getBytes("UTF-8"), Base64.DEFAULT));
				if (TextByte != null) {
					mPasswordEditText.setText(new String(TextByte, "UTF-8"));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
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
				mIdTextInputLayout.setError(getString(R.string.check_login_hint));
				mIdTextInputLayout.setErrorEnabled(true);
				mPasswordTextInputLayout.setError(getString(R.string.check_login_hint));
				mPasswordTextInputLayout.setErrorEnabled(true);
			}

			@Override
			public void onSuccess() {
				super.onSuccess();

				progressDialog.dismiss();
				try {
					Memory.setString(LoginActivity.this, Constant.PREF_USERNAME, id);
					byte[] TextByte = Utils.EncryptAES(Constant.IvAES.getBytes("UTF-8"),
							Constant.KeyAES.getBytes("UTF-8"), pwd.getBytes("UTF-8"));
					if (TextByte == null) {
						Memory.setString(LoginActivity.this, Constant.PREF_PASSWORD, "");
					} else {
						String newPwd = Base64.encodeToString(TextByte, Base64.DEFAULT);
						Log.d(Constant.TAG, Base64.encodeToString(TextByte, Base64.DEFAULT));
						Memory.setString(LoginActivity.this, Constant.PREF_PASSWORD,
								mRememberCheckBox.isChecked() ? newPwd : "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

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
