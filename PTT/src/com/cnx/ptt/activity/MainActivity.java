package com.cnx.ptt.activity;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.cnx.ptt.Constants;
import com.cnx.ptt.R;
import com.cnx.ptt.activity.BaseActivity.UserSession;
import com.cnx.ptt.chat.service.XmppService;
import com.cnx.ptt.utils.BroadcastHelper;
import com.cnx.ptt.utils.L;
/**
 * PTT 主页面，含有三个tab, 三个tab 跳转到不同的功能模块
 * @author yong.xu
 *
 */
public class MainActivity extends TabActivity{
	private Intent intent;
	private TabHost.TabSpec tabSpec;
	private View tabIndicator;
	private TextView title;
	private ImageView icon;
	private XmppService mXxService;// Main服务
	//三个TAB 标题
	private String[] tabTitle = {"Message", "Explore", "Contact"};
	// 三个TAB 对应的不同的图片选择器
	private int[] tabResource = {R.drawable.tab_message_selector, R.drawable.tab_explore_selector, R.drawable.tab_contact_selector};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//1. 载入main布局文件
		setContentView(R.layout.activity_main);
		//2. 获取tabhost
		final TabHost tabHost = getTabHost();
		//3. 遍历3个tab, 加载各自的内容
		for(int i = 0; i < 3; i ++){
			//？？？这个地方疑问： 相同的布局文件加载了三次，损耗系统资源，如果放在循环外，会出现异常，大致意思是 tabIndicator 已经被绑定，tabSpec.setIndicator(tabIndicator);  若再次绑定，需先解绑定
			tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
			title = (TextView) tabIndicator.findViewById(R.id.title);
			icon = (ImageView) tabIndicator.findViewById(R.id.icon);
			
			tabSpec = tabHost.newTabSpec(tabTitle[i]);
			switch (i) {
			case 1:
				intent = new Intent(MainActivity.this, ExploreTabActivity.class);
				break;
			case 2:
				intent = new Intent(MainActivity.this, ContactTabActivity.class);
				break;
			default:
				intent = new Intent(MainActivity.this, MessageTabActivity.class);
				break;
			}
			
			title.setText(tabTitle[i]);
			icon.setImageResource(tabResource[i]);
			tabSpec.setIndicator(tabIndicator);
			tabSpec.setContent(intent);
			tabHost.addTab(tabSpec);
		}
		tabHost.setCurrentTab(0);
		updateTab(tabHost);
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				updateTab(tabHost);
			}
			
		});
		getOverflowMenu();
		
		bindXMPPService();
		pollingFollowTask();
	}
	 /**
     * 更新Tab标签的颜色，和字体的颜色
     * @param tabHost
     */ 
    private void updateTab(final TabHost tabHost) { 
    	
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) { 
        	
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.title); 
            
            if (tabHost.getCurrentTab() == i) {//选中  
            	//view.setBackgroundDrawable(getResources().getDrawable(R.drawable.category_current));//选中后的背景  
                tv.setTextColor(this.getResources().getColorStateList(R.color.color_tab_title)); 
            } else {//不选中  
            	//view.setBackgroundDrawable(getResources().getDrawable(R.drawable.category_bg));//非选择的背景  
                tv.setTextColor(Color.GRAY); 
            } 
        } 
    } 
    /**
	 * 绑定服务
	 */
	private void bindXMPPService() {
		Intent mServiceIntent = new Intent(this, XmppService.class);
		
		bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}
	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXxService = ((XmppService.XXBinder) service).getService();
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mXxService.unRegisterConnectionStatusCallback();
			mXxService = null;
		}

	};
	/**
	 * 定时去数据库查询所关注的task状态， 并
	 */
	private void pollingFollowTask() {
		final Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				if(mXxService != null){
					BroadcastHelper.sendBroadCast(getApplicationContext(), Constants.POLLING_FOLLOW_TASK, null, null);
					L.i("Send broadcast to polling task");
				}else {
					timer.cancel();
				}
				
			}
		};
		//启动之后30秒开始第一次轮询， 以后间隔10分钟查询一下
		timer.schedule(task , 30000, Constants.POLLING_PERIOD);
	}

	/**
	 * 显示actionBar
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	/**
	 * actionBar 点击事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
		
		case R.id.menu_profile:
			
			return true;
		case R.id.menu_logout:
			if (mXxService != null) {
				mXxService.logout();
				mXxService.stopSelf();
			}
			this.finish();
			return true;
			
		case R.id.menu_about:
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.menu_profile);
		item.setIcon(R.drawable.ic_launcher);
		item.setTitle(UserSession.user.getUser_email());
		return true;
	}
	private void getOverflowMenu() {  
		   
	     try {  
	        ViewConfiguration config = ViewConfiguration.get(this);  
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");  
	        if(menuKeyField != null) {  
	            menuKeyField.setAccessible(true);  
	            menuKeyField.setBoolean(config, false);  
	        }  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
	}  
}