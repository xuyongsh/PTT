package com.cnx.ptt.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cnx.ptt.R;
import com.cnx.ptt.activity.BaseActivity.UserSession;
import com.cnx.ptt.chat.service.IConnectionStatusCallback;
import com.cnx.ptt.chat.service.XmppService;
import com.cnx.ptt.chat.utils.PreferenceConstants;
import com.cnx.ptt.chat.utils.PreferenceUtils;
import com.cnx.ptt.pojo.User;
import com.cnx.ptt.utils.DialogUtil;
import com.cnx.ptt.utils.L;
import com.cnx.ptt.utils.T;

public class LoginActivity extends FragmentActivity implements IConnectionStatusCallback{

	private String TAG = "LoginActivity";
	private EditText et_email;
	private EditText et_password;
	private LinearLayout login_form;
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
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
//		if (mLoginDialog != null && mLoginDialog.isShowing()){
//			mLoginDialog.dismiss();
//		}
		
		if (connectedState == XmppService.CONNECTED) {
			//save to sharedpereference ....
			save2Preferences();
			
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}else if (connectedState == XmppService.DISCONNECTED){
			T.showLong(LoginActivity.this, "Login failed!" + reason);
		}
	}
//	private CheckBox cb_rememberme;
	private void save2Preferences() {
		boolean isAutoSavePassword = true;
//		boolean isUseTls = mUseTlsCK.isChecked();
//		boolean isSilenceLogin = mSilenceLoginCK.isChecked();
//		boolean isHideLogin = mHideLoginCK.isChecked();
		PreferenceUtils.setPrefBoolean(this, PreferenceConstants.AUTOSAVEPWD, isAutoSavePassword);
		PreferenceUtils.setPrefString(this, PreferenceConstants.ACCOUNT, mEmail);// 帐号是一直保存的
		if (isAutoSavePassword){
			PreferenceUtils.setPrefString(this, PreferenceConstants.PASSWORD, mPassword);
		}
		else{
			PreferenceUtils.setPrefString(this, PreferenceConstants.PASSWORD, "");
		}
		//将用户信息保存到session
		UserSession.user = new User(mEmail, mPassword);
		
	}
	/**
	 * 初始化login View, 绑定点击事件
	 */
	private void initView() { 
		// 初始化用户名密码控件
		et_email = (EditText) findViewById(R.id.et_email);
		et_password = (EditText) findViewById(R.id.et_password);
		//cb_rememberme = (CheckBox) findViewById(R.id.cb_rememberme);
		login_form = (LinearLayout) findViewById(R.id.login_form);
		boolean isAutoSavePassword = PreferenceUtils.getPrefBoolean(this, PreferenceConstants.AUTOSAVEPWD, false);
		if(isAutoSavePassword){
			et_email.setText(PreferenceUtils.getPrefString(this, PreferenceConstants.ACCOUNT, ""));
			et_password.setText(PreferenceUtils.getPrefString(this, PreferenceConstants.PASSWORD, ""));
			//cb_rememberme.setChecked(true);
		}
		//实例化 dialog
		mLoginDialog = DialogUtil.getLoginDialog(this);
		//实例化button
		findViewById(R.id.bt_login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				login_form.setVisibility(View.INVISIBLE);
				mLoginDialog.show();
				attemptLogin();
			}
		});
		/*findViewById(R.id.bt_forget).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				forgetPWD();
			}
		});*/
	}

	/**
	 * 用户登录，获取用户名密码，并判断有效性
	 */
	private void attemptLogin() {
		// get the values at the time of the login attempt.
		mEmail = et_email.getText().toString().trim();
		mPassword = et_password.getText().toString().trim();
//		mRememberMe = cb_rememberme.isChecked();
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
			if(xmppService != null){
				String account = splitEmail(mEmail);
				xmppService.Login(account, mPassword);
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
//				if (mLoginDialog != null && mLoginDialog.isShowing())
//					mLoginDialog.dismiss();
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
	@Override
	public void onBackPressed() {
		this.finish();
		System.exit(0);
		super.onBackPressed();
	}
}
