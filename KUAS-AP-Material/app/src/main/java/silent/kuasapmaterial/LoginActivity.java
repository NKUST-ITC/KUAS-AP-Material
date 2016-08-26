package silent.kuasapmaterial;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.kuas.ap.R;

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
		implements TextView.OnEditorActionListener, View.OnClickListener {

	TextInputLayout mIdTextInputLayout, mPasswordTextInputLayout;
	EditText mIdEditText, mPasswordEditText;
	ImageView dot_ap, dot_leave, dot_bus;
	CheckBox mRememberCheckBox;
	TextView mVersionTextView;
	Button mLoginButton;

	String version;

	AlertDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		clearUserData();
		init(R.string.app_name, R.layout.activity_login);

		initGA("Login Screen");
		findViews();
		setUpViews();
		getVersion();
		checkServerStatus();
		getNews();
	}

	private void checkUpdateNote(String version) {
		if (!Memory.getString(this, Constant.PREF_UPDATE_NOTE, "")
				.equals(getString(R.string.update_note_content))) {
			new AlertDialog.Builder(this).setTitle(getString(R.string.update_note_title, version))
					.setMessage(R.string.update_note_content).setPositiveButton(R.string.ok, null)
					.show();
			Memory.setString(this, Constant.PREF_UPDATE_NOTE,
					getString(R.string.update_note_content));
		}
	}

	private void getVersion() {
		try {
			PackageInfo pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pkgInfo.versionName;
			mVersionTextView.setText(getString(R.string.version, version));
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			version = "1.0.0";
			mVersionTextView.setText(getString(R.string.version, "1.0.0"));
		}
		checkUpdateNote(getString(R.string.version, version));

		Helper.getAppVersion(this, new GeneralCallback() {

			@Override
			public void onSuccess(String data) {
				super.onSuccess(data);

				if (isFinishing()) {
					return;
				}

				String[] serverVersions = data.split("\\.");
				String[] currentVersions = version.split("\\.");

				if (Integer.valueOf(serverVersions[0]) > Integer.valueOf(currentVersions[0])) {
					Utils.createForceUpdateDialog(LoginActivity.this).show();
				} else if (serverVersions[0].equals(currentVersions[0])) {
					if (Integer.valueOf(serverVersions[1]) > Integer.valueOf(currentVersions[1])) {
						Utils.createForceUpdateDialog(LoginActivity.this).show();
					} else if (serverVersions[1].equals(currentVersions[1])) {
						if (Integer.valueOf(serverVersions[2]) >
								Integer.valueOf(currentVersions[2])) {
							if (Integer.valueOf(serverVersions[2]) -
									Integer.valueOf(currentVersions[2]) >= 5) {
								Utils.createForceUpdateDialog(LoginActivity.this).show();
							} else {
								Utils.createUpdateDialog(LoginActivity.this).show();
							}
						}
					}
				}
			}
		});
	}

	private void checkServerStatus() {
		Helper.getServerStatus(this, new ServerStatusCallback() {

			@Override
			public void onSuccess(ServerStatusModel model) {
				super.onSuccess(model);

				if (isFinishing()) {
					return;
				}

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

				if (isFinishing()) {
					return;
				}

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
		mIdTextInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout_id);
		mPasswordEditText = (EditText) findViewById(R.id.editText_password);
		mPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout_password);

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
		mRememberCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!mRememberCheckBox.isChecked()) {
					Memory.setString(LoginActivity.this, Constant.PREF_PASSWORD, "");
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_login) {
			String id = mIdEditText.getText().toString();
			if (id.length() != 0 && id.length() < 10) {
				mTracker.send(
						new HitBuilders.EventBuilder().setCategory("login").setAction("status")
								.setLabel("teacher").build());
				new AlertDialog.Builder(this).setTitle(R.string.teacher_confirm_title)
						.setMessage(R.string.teacher_confirm_content)
						.setPositiveButton(R.string.continue_to_use,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										login();
									}
								}).setNegativeButton(R.string.cancel, null).show();
			} else {
				login();
			}
		}
	}

	private void login() {
		mTracker.send(
				new HitBuilders.EventBuilder().setCategory("login").setAction("click").build());

		mIdTextInputLayout.setErrorEnabled(false);
		mPasswordTextInputLayout.setErrorEnabled(false);

		final String id = mIdEditText.getText().toString();
		final String pwd = mPasswordEditText.getText().toString();

		if (TextUtils.isEmpty(id)) {
			mIdTextInputLayout.setError(getString(R.string.enter_username_hint));
			mIdTextInputLayout.setErrorEnabled(true);
			return;
		}
		if (TextUtils.isEmpty(pwd)) {
			mPasswordTextInputLayout.setError(getString(R.string.enter_password_hint));
			mPasswordTextInputLayout.setErrorEnabled(true);
			return;
		}

		Memory.setBoolean(this, Constant.PREF_REMEMBER_PASSWORD, mRememberCheckBox.isChecked());
		mProgressDialog = Utils.createLoadingDialog(this, R.string.login_ing);
		mProgressDialog.show();
		Helper.login(this, id, pwd, new GeneralCallback() {

			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);

				if (isFinishing()) {
					return;
				}
				Utils.dismissDialog(mProgressDialog);
				Toast.makeText(LoginActivity.this, R.string.timeout_message, Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onTokenExpired() {
				super.onTokenExpired();

				if (isFinishing()) {
					return;
				}
				Utils.dismissDialog(mProgressDialog);
				mIdTextInputLayout.setError(getString(R.string.check_login_hint));
				mIdTextInputLayout.setErrorEnabled(true);
				mPasswordTextInputLayout.setError(getString(R.string.check_login_hint));
				mPasswordTextInputLayout.setErrorEnabled(true);
			}

			@Override
			public void onSuccess() {
				super.onSuccess();

				if (isFinishing()) {
					return;
				}
				Utils.dismissDialog(mProgressDialog);
				try {
					Memory.setString(LoginActivity.this, Constant.PREF_USERNAME, id);
					byte[] TextByte = Utils.EncryptAES(Constant.IvAES.getBytes("UTF-8"),
							Constant.KeyAES.getBytes("UTF-8"), pwd.getBytes("UTF-8"));
					if (TextByte == null) {
						Memory.setString(LoginActivity.this, Constant.PREF_PASSWORD, "");
					} else {
						String newPwd = Base64.encodeToString(TextByte, Base64.DEFAULT);
						Memory.setString(LoginActivity.this, Constant.PREF_PASSWORD,
								mRememberCheckBox.isChecked() ? newPwd : "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Memory.setBoolean(LoginActivity.this, Constant.PREF_IS_LOGIN, true);
				Crashlytics.setUserName(id);
				startActivity(new Intent(LoginActivity.this, LogoutActivity.class));
			}
		});
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v.getId() == R.id.editText_password) {
			String id = mIdEditText.getText().toString();
			if (id.length() != 0 && id.length() < 10) {
				mTracker.send(
						new HitBuilders.EventBuilder().setCategory("login").setAction("status")
								.setLabel("teacher").build());
				new AlertDialog.Builder(this).setTitle(R.string.teacher_confirm_title)
						.setMessage(R.string.teacher_confirm_content)
						.setPositiveButton(R.string.continue_to_use,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										login();
									}
								}).setNegativeButton(R.string.cancel, null).show();
			} else {
				login();
			}
			return true;
		}
		return false;
	}
}
