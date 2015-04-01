package com.cnx.ptt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cnx.ptt.R;
import com.cnx.ptt.TrackTimeActivity;

public class ExploreTabActivity extends BaseActivity{
	private ListView lv_explore_list;
	String[] exploreText = {"Task Monitor"};
	int[] exploreIcons = {R.drawable.ic_action_important};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explore_tab);
		lv_explore_list = (ListView) findViewById(R.id.lv_explore_list);
		lv_explore_list.setAdapter(new ExploreAdaptor());
		lv_explore_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch (position) {
				
				
				case 2:
					intent = new Intent(ExploreTabActivity.this, TaskMonitorActivity.class);
					break;
				default:
					intent = new Intent(ExploreTabActivity.this, TaskMonitorActivity.class);
					break;
				}
				startActivity(intent); 
				
			}
		});
	}
	/**
	 * explore tab adapter
	 * @author yong.xu
	 *
	 */
	private class ExploreAdaptor extends BaseAdapter {

		@Override
		public int getCount() {
			return exploreText.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView==null){
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.explore_item, null);
				holder = new ViewHolder();
				holder.iv_tab_icon = (ImageView) convertView.findViewById(R.id.iv_tab_icon);
				holder.tv_tab_text = (TextView) convertView.findViewById(R.id.tv_tab_text);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder)convertView.getTag();
			}
			holder.iv_tab_icon.setBackgroundResource(exploreIcons[position]);
			holder.tv_tab_text.setText(exploreText[position]);
			return convertView;
		}

	}
	private final class ViewHolder
	{
		public TextView tv_tab_text;
	    public ImageView iv_tab_icon;
	}
}
