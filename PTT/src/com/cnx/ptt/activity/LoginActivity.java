package com.cnx.ptt.activity;

import java.net.URLEncoder;

import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cnx.ptt.R;
import com.cnx.ptt.http.HttpUtil;
import com.cnx.ptt.http.Url;
import com.cnx.ptt.http.json.LoginJson;
import com.cnx.ptt.pojo.User;
import com.cnx.ptt.utils.LogUtils;
import com.cnx.ptt.xmpp.XmppConnectionManager;

public class LoginActivity extends BaseActivity implements OnClickListener{
	
	private String TAG = "LoginActivity";
	
	public static final String LOGIN_ACTION = "com.cnx.ptt.action.LOGIN";
	private View mLoginStatusView;
	private View mLoginFormView;
	
	private EditText et_email;
	private EditText et_password;
	private CheckBox cb_rememberme;
	// define email, password
	private String mEmail;
	private String mPassword;
	private boolean mRememberMe;

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// 初始化用户名密码控件
		et_email = (EditText) findViewById(R.id.et_email);
		et_password = (EditText) findViewById(R.id.et_password);
		cb_rememberme = (CheckBox) findViewById(R.id.cb_rememberme);
		
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginFormView = findViewById(R.id.login_form);

		initView();
	}

	/**
	 * 初始化login View, 绑定点击事件
	 */
	private void initView() {
		// 检查是否有登录记录
		loadUserIfRemember();
		findViewById(R.id.bt_login).setOnClickListener(this);
		findViewById(R.id.bt_forget).setOnClickListener(this);

	}

	/**
	 * 检查是否已经登录过并且保存缓存，如果存在，就会把对应的用户名，密码 自动填入对话框
	 */
	private void loadUserIfRemember() {
		sp = getSharedPreferences("config", MODE_PRIVATE);

		String email = sp.getString("email", null);
		String password = sp.getString("password", null);
		Boolean rememberme = sp.getBoolean("rememberme", false);

		if (rememberme && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
			et_email.setText(email);
			et_password.setText(password);
			cb_rememberme.setChecked(rememberme);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_login:
			showProgress(true, mLoginStatusView, mLoginFormView);
			attemptLogin();
			break;
		case R.id.bt_forget:
			forgetPWD();
			break;
		default:
			break;
		}
	}
	/**
	 * 用户登录，获取用户名密码，并判断有效性
	 */
	private void attemptLogin() {
		// get the values at the time of the login attempt.
		mEmail = et_email.getText().toString().trim();
		mPassword = et_password.getText().toString().trim();
		mRememberMe = cb_rememberme.isChecked();
		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			et_password.setError(getString(R.string.error_field_required));
			focusView = et_password;
			cancel = true;
		} else if (mPassword.length() < 4) {
			et_password.setError(getString(R.string.error_invalid_password));
			focusView = et_password;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			et_email.setError(getString(R.string.error_field_required));
			focusView = et_email;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			et_email.setError(getString(R.string.error_invalid_email));
			focusView = et_email;
			cancel = true;
		}
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mAuthTask = new UserLoginTask();
			mAuthTask.execute();
		}
	}

	/**
	 * forget password function
	 */
	private void forgetPWD() {
		Toast.makeText(this, "Hi, here is PTT forget password function!", 1)
				.show();
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<String, String, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {

			User user = null;
			String result;
			try {
				String urlString = Url.LOGIN_URL;
				result = HttpUtil.getByHttpClient(LoginActivity.this,
						urlString, new BasicNameValuePair("username",
								URLEncoder.encode(mEmail, "utf-8")),
						new BasicNameValuePair("password", mPassword));
				

				user = getResult(result);
				
				// login xmpp server
				if (loginXmppServer()) {
					System.out.println("XMPP init error");
				}
				if (user == null) {
					return false;
				} else {
					UserSession.user = user;
					Editor editor = sp.edit();
					
					editor.putString("email", mEmail);
					editor.putString("password", mPassword);
					editor.putBoolean("rememberme", mRememberMe);
					editor.commit();
					// Do we need to save user info into SQLite database?????
					// saveUserToLocal(user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}

		public User getResult(String result) {

			return LoginJson.instance(LoginActivity.this).readJsonLoginModels(
					result);

		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
				showProgress(false, mLoginStatusView, mLoginFormView);
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				et_password
						.setError(getString(R.string.error_incorrect_password));
				et_password.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}

	/**
	 * 用户登录到xmpp服务器， 登录的逻辑重新改进
	 * 1. 登录
	 * 2. 注册监听
	 * @return boolean
	 */
	private boolean loginXmppServer() {
		try {
			// TODO: Can not login with @cn.ibm.com prefix, will be lost
			// connection
			// 1. connection to xmpp server
			String userString = mEmail.substring(0, mEmail.indexOf("@"));
			XmppConnectionManager.getConnection().login(userString, mPassword);

			// tell server user status
			Presence presence = new Presence(Presence.Type.available);
			presence.setPriority(1);
			XmppConnectionManager.getConnection().sendPacket(presence);
			
			return true;
		} catch (XMPPException e) {
			LogUtils.i("loginXmppServer exception",	"login XMPP server is disconnected.");
			XmppConnectionManager.closeConnection();
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	
}
