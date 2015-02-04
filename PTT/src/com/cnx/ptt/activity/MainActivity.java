package com.cnx.ptt.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.cnx.ptt.R;
/**
 * PTT 主页面，含有三个tab, 三个tab 跳转到不同的功能模块
 * @author yong.xu
 *
 */
public class MainActivity extends TabActivity{
	Intent intent;
	TabHost.TabSpec tabSpec;
	View tabIndicator;
	TextView title;
	ImageView icon;
	//三个TAB 标题
	String[] tabTitle = {"Message", "Explore", "Contact"};
	// 三个TAB 对应的不同的图片选择器
	int[] tabResource = {R.drawable.tab_message_selector, R.drawable.tab_explore_selector, R.drawable.tab_contact_selector};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//1. 载入main布局文件
		setContentView(R.layout.activity_main);
		//2. 获取tabhost
		TabHost tabHost = getTabHost();
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
		case R.id.action_search:
			// do something for new button
			return true;
		case R.id.action_new:
			// do something for new button
			return true;
		case R.id.action_settings:
			Intent i = new Intent(this.getBaseContext(), SettingsActivity.class);
			this.startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}