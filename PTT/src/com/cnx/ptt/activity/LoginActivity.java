package com.cnx.ptt.activity;

import java.net.URLEncoder;

import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cnx.ptt.R;
import com.cnx.ptt.chat.service.IConnectionStatusCallback;
import com.cnx.ptt.chat.service.XmppService;
import com.cnx.ptt.chat.utils.PreferenceConstants;
import com.cnx.ptt.chat.utils.PreferenceUtils;
import com.cnx.ptt.http.HttpUtil;
import com.cnx.ptt.http.Url;
import com.cnx.ptt.http.json.LoginJson;
import com.cnx.ptt.pojo.User;
import com.cnx.ptt.utils.DialogUtil;
import com.cnx.ptt.utils.L;
import com.cnx.ptt.utils.T;
import com.cnx.ptt.xmpp.XmppConnectionManager;

public class LoginActivity extends FragmentActivity implements IConnectionStatusCallback{

	private String TAG = "LoginActivity";
	private View mLoginStatusView;
	private View mLoginFormView;

	private EditText et_email;
	private EditText et_password;
	
	// define email, password
	private String mEmail;
	private String mPassword;
	private boolean mRememberMe;
	//XMPP login action
	public static final String LOGIN_ACTION = "com.cnx.ptt.action.LOGIN";
	//定义xmpp service 
	XmppService xmppService;
//定义login dialog
	private Dialog mLoginDialog;

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
		
		//xmpp service
		startService(new Intent(LoginActivity.this, XmppService.class));
		bindXMPPService();
		
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		
	}

	@Override
	protected void onDestroy() {
		unbindXMPPService();
		super.onDestroy();
		if (mLoginOutTimeProcess != null) {
			mLoginOutTimeProcess.stop();
			mLoginOutTimeProcess = null;
		}
	}
	private void bindXMPPService() {
		L.i(LoginActivity.class, "bindXMPPService");
		Intent mServiceIntent = new Intent(this, XmppService.class);
		mServiceIntent.setAction(LOGIN_ACTION);
		bindService(mServiceIntent, xmppServiceConn, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}
	private void unbindXMPPService() {
		try {
			unbindService(xmppServiceConn);
			L.i(LoginActivity.class, "[XMPPService] Unbind");
		} catch (IllegalArgumentException e) {
			L.e(LoginActivity.class, "Service wasn't bound!");
		}
	}
	ServiceConnection xmppServiceConn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			xmppService = ((XmppService.XXBinder)service).getService();
			xmppService.registerConnectionStatusCallback(LoginActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			xmppService.unRegisterConnectionStatusCallback();
			xmppService = null;
		}
		
	};
	
	/**
	 * 连接状态
	 */
	
	@Override
	public void connectionStatusChanged(int connectedState, String reason) {
		if (mLoginDialog != null && mLoginDialog.isShowing()){
			mLoginDialog.dismiss();
		}
		if (connectedState == XmppService.CONNECTED) {
			//save to sharedpereference ....
			save2Preferences();
			
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}else if (connectedState == XmppService.DISCONNECTED)
			T.showLong(LoginActivity.this, "Login failed!" + reason);
	}
	private CheckBox cb_rememberme;
	private void save2Preferences() {
		boolean isAutoSavePassword = cb_rememberme.isChecked();
//		boolean isUseTls = mUseTlsCK.isChecked();
//		boolean isSilenceLogin = mSilenceLoginCK.isChecked();
//		boolean isHideLogin = mHideLoginCK.isChecked();
		PreferenceUtils.setPrefString(this, PreferenceConstants.ACCOUNT,
				mEmail);// 帐号是一直保存的
		if (isAutoSavePassword)
			PreferenceUtils.setPrefString(this, PreferenceConstants.PASSWORD,
					mPassword);
		else
			PreferenceUtils.setPrefString(this, PreferenceConstants.PASSWORD,
					"");

		
	}
	/**
	 * 初始化login View, 绑定点击事件
	 */
	private void initView() {
		// 检查是否有登录记录
//		loadUserIfRemember();
		//实例化 dialog
		mLoginDialog = DialogUtil.getLoginDialog(this);
		
		//实例化button
		findViewById(R.id.bt_login).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				attemptLogin();
			}
		});
		findViewById(R.id.bt_forget).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				forgetPWD();
				
			}
		});
	}

	/**
	 * 检查是否已经登录过并且保存缓存，如果存在，就会把对应的用户名，密码 自动填入对话框
	 */
	private void loadUserIfRemember() {
		/*sp = getSharedPreferences("config", MODE_PRIVATE);

		String email = sp.getString("email", null);
		String password = sp.getString("password", null);
		Boolean rememberme = sp.getBoolean("rememberme", false);

		if (rememberme && !TextUtils.isEmpty(email)
				&& !TextUtils.isEmpty(password)) {
			et_email.setText(email);
			et_password.setText(password);
			cb_rememberme.setChecked(rememberme);
		}*/
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
//			 mAuthTask = new UserLoginTask();
//			 mAuthTask.execute();
			
			if(xmppService != null){
				mEmail = splitEmail(mEmail);
				xmppService.Login(mEmail, mPassword);
			}
		}
	}
	private String splitEmail(String email) {
		if (!email.contains("@"))
			return email;
		
		String[] res = email.split("@");
		String userName = res[0];
		
		
		return userName;
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
//					UserSession.user = user;
//					Editor editor = sp.edit();
//
//					editor.putString("email", mEmail);
//					editor.putString("password", mPassword);
//					editor.putBoolean("rememberme", mRememberMe);
//					editor.commit();
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
//				showProgress(false, mLoginStatusView, mLoginFormView);
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
	 * 用户登录到xmpp服务器， 登录的逻辑重新改进 1. 登录 2. 注册监听
	 * 
	 * @return boolean
	 */
	private boolean loginXmppServer() {

		try {
			// TODO: Can not login with @cn.ibm.com prefix, will be lost //
			// 1. connection to xmpp server
			String userAccount = mEmail.substring(0, mEmail.indexOf("@"));
			XmppConnectionManager.getConnection().login(userAccount, mPassword);

			// tell server user status Presence presence = new
			Presence presence = new Presence(Presence.Type.available);
			presence.setPriority(1);
			XmppConnectionManager.getConnection().sendPacket(presence);

			return true;
		} catch (XMPPException e) {
			L.i("loginXmppServer exception", "login XMPP server is disconnected.");
			XmppConnectionManager.closeConnection();
			e.printStackTrace();
		}
		return false;
	}
	private ConnectionOutTimeProcess mLoginOutTimeProcess;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOGIN_OUT_TIME:
				if (mLoginOutTimeProcess != null
						&& mLoginOutTimeProcess.running)
					mLoginOutTimeProcess.stop();
				if (mLoginDialog != null && mLoginDialog.isShowing())
					mLoginDialog.dismiss();
				T.showShort(LoginActivity.this, R.string.timeout_try_again);
				break;

			default:
				break;
			}
		}

	};
	private static final int LOGIN_OUT_TIME = 0;
	// 登录超时处理线程
	class ConnectionOutTimeProcess implements Runnable {
		public boolean running = false;
		private long startTime = 0L;
		private Thread thread = null;

		ConnectionOutTimeProcess() {
		}

		public void run() {
			while (true) {
				if (!this.running)
					return;
				if (System.currentTimeMillis() - this.startTime > 20 * 1000L) {
					mHandler.sendEmptyMessage(LOGIN_OUT_TIME);
				}
				try {
					Thread.sleep(10L);
				} catch (Exception localException) {
				}
			}
		}

		public void start() {
			try {
				this.thread = new Thread(this);
				this.running = true;
				this.startTime = System.currentTimeMillis();
				this.thread.start();
			} finally {
			}
		}

		public void stop() {
			try {
				this.running = false;
				this.thread = null;
				this.startTime = 0L;
			} finally {
			}
		}
	}
}
