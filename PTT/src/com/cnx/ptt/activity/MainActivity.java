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
 * PTT ��ҳ�棬��������tab, ����tab ��ת����ͬ�Ĺ���ģ��
 * @author yong.xu
 *
 */
public class MainActivity extends TabActivity{
	private Intent intent;
	private TabHost.TabSpec tabSpec;
	private View tabIndicator;
	private TextView title;
	private ImageView icon;
	private XmppService mXxService;// Main����
	//����TAB ����
	private String[] tabTitle = {"Message", "Explore", "Contact"};
	// ����TAB ��Ӧ�Ĳ�ͬ��ͼƬѡ����
	private int[] tabResource = {R.drawable.tab_message_selector, R.drawable.tab_explore_selector, R.drawable.tab_contact_selector};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//1. ����main�����ļ�
		setContentView(R.layout.activity_main);
		//2. ��ȡtabhost
		final TabHost tabHost = getTabHost();
		//3. ����3��tab, ���ظ��Ե�����
		for(int i = 0; i < 3; i ++){
			//����������ط����ʣ� ��ͬ�Ĳ����ļ����������Σ����ϵͳ��Դ���������ѭ���⣬������쳣��������˼�� tabIndicator �Ѿ����󶨣�tabSpec.setIndicator(tabIndicator);  ���ٴΰ󶨣����Ƚ��
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
     * ����Tab��ǩ����ɫ�����������ɫ
     * @param tabHost
     */ 
    private void updateTab(final TabHost tabHost) { 
    	
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) { 
        	
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.title); 
            
            if (tabHost.getCurrentTab() == i) {//ѡ��  
            	//view.setBackgroundDrawable(getResources().getDrawable(R.drawable.category_current));//ѡ�к�ı���  
                tv.setTextColor(this.getResources().getColorStateList(R.color.color_tab_title)); 
            } else {//��ѡ��  
            	//view.setBackgroundDrawable(getResources().getDrawable(R.drawable.category_bg));//��ѡ��ı���  
                tv.setTextColor(Color.GRAY); 
            } 
        } 
    } 
    /**
	 * �󶨷���
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
	 * ��ʱȥ���ݿ��ѯ����ע��task״̬�� ��
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
		//����֮��30�뿪ʼ��һ����ѯ�� �Ժ���10���Ӳ�ѯһ��
		timer.schedule(task , 30000, Constants.POLLING_PERIOD);
	}

	/**
	 * ��ʾactionBar
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	/**
	 * actionBar ����¼�
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