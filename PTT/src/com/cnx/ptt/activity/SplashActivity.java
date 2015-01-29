package com.cnx.ptt.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.cnx.ptt.R;
import com.cnx.ptt.http.NetWorkHelper;

/**
 * 初始化动画
 * 此activity 主要作用
 * 1. 检查网络
 * 2. 显示版本纤细
 * 3. 版本升级
 * 
 * @author IBM_ADMIN
 *
 */
public class SplashActivity extends BaseActivity {
	private TextView tv_splash_version;
	private TextView tv_update_info;
	
	private Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		//显示版本信息
		tv_splash_version.setText("version "+getVersion());
		//启动页面动画效果
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1000);
		
		findViewById(R.id.rl_root_splash).startAnimation(aa);
//		tv_update_info = (TextView) findViewById(R.id.tv_update_info);
		//check network is available
		NetWorkHelper.checkNetwork(SplashActivity.this);
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				enterLogin();
			}

		}, 2000);
		
	}
	/**
	 * enter login page
	 */
	private void enterLogin() {
		Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**
	 * get version of this application
	 */
	private String getVersion(){
		//packageManager 用来管理手机APK
		PackageManager pm = getPackageManager();
		//得到APK的清单文件
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
}
