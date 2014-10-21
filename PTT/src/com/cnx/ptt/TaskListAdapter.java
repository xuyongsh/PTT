package com.cnx.ptt;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskListAdapter extends BaseAdapter{
	
	private Context context;  
    private List<TaskListItem> list;
    private TextView tv_name;
    private TextView tv_news;
    private TextView tv_time;
    
    public TaskListAdapter(Context c, List<TaskListItem> l){
    	this.context = c;
    	this.list = l;
    }

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String item_name = this.list.get(position).getM_name();
		String item_news = this.list.get(position).getM_news();
		String item_time = this.list.get(position).getM_time();
		
		convertView = LayoutInflater.from(this.context).inflate(R.layout.task_list_item, null);
		
		this.tv_name = (TextView) convertView.findViewById(R.id.task_list_item_name);
		this.tv_news = (TextView) convertView.findViewById(R.id.task_list_item_news);
		this.tv_time = (TextView) convertView.findViewById(R.id.task_list_item_time);
		
		this.tv_name.setText(item_name);
		this.tv_news.setText(item_news);
		this.tv_time.setText(item_time);
		
		return convertView;
	}

}
