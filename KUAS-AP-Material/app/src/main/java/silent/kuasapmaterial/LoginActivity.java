package silent.kuasapmaterial;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import silent.kuasapmaterial.callback.GeneralCallback;
import silent.kuasapmaterial.callback.ServerStatusCallback;
import silent.kuasapmaterial.libs.Helper;
import silent.kuasapmaterial.models.ServerStatusModel;

public class LoginActivity extends AppCompatActivity
		implements TextView.OnEditorActionListener, View.OnClickListener {

	Toolbar toolbar;
	TextInputLayout mIdTextInputLayout, mPasswordTextInputLayout;
	EditText mIdEditText, mPasswordEditText;
	ImageView dot_ap, dot_leave, dot_bus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViews();
		setUpViews();
		checkServerStatus();
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

	private void findViews() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);

		mIdEditText = (EditText) findViewById(R.id.id_editText);
		mIdTextInputLayout = (TextInputLayout) mIdEditText.getParent();
		mPasswordEditText = (EditText) findViewById(R.id.password_editText);
		mPasswordTextInputLayout = (TextInputLayout) mPasswordEditText.getParent();

		dot_ap = (ImageView) findViewById(R.id.dot_ap);
		dot_leave = (ImageView) findViewById(R.id.dot_leave);
		dot_bus = (ImageView) findViewById(R.id.dot_bus);
	}

	private void setUpViews() {
		mPasswordEditText.setOnEditorActionListener(this);
		mIdTextInputLayout.setHint(getString(R.string.id_hint));
		mPasswordTextInputLayout.setHint(getString(R.string.password_hint));

		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle(R.string.app_name);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.login_button) {
			login();
		}
	}

	private void login() {
		mIdTextInputLayout.setErrorEnabled(false);
		mPasswordTextInputLayout.setErrorEnabled(false);

		String id = mIdEditText.getText().toString();
		String pwd = mPasswordEditText.getText().toString();

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

		Helper.login(this, id, pwd, new GeneralCallback() {
			@Override
			public void onFail(String errorMessage) {
				super.onFail(errorMessage);
				Toast.makeText(LoginActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess() {
				super.onSuccess();
				Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v.getId() == R.id.password_editText) {
			login();
			return true;
		}
		return false;
	}
}
