package com.cnx.adapter;

import java.util.ArrayList;

import com.cnx.pojo.TaskListItem;
import com.cnx.ptt.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskListAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater;  
    private ArrayList<TaskListItem> taskList;
    
    public TaskListAdapter(Context context, ArrayList<TaskListItem> list){
    	mInflater = LayoutInflater.from(context);
    	taskList = list;
    }
 
	@Override
	public int getCount() {
		return taskList.size();
	}

	@Override
	public Object getItem(int position) {
		return taskList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.task_list_item, null);
			holder = new ViewHolder();
			holder.item_reqnum = (TextView)convertView.findViewById(R.id.task_list_item_name);
			holder.item_title = (TextView)convertView.findViewById(R.id.task_list_item_news);
			holder.item_subdate=(TextView)convertView.findViewById(R.id.task_list_item_time);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.item_reqnum.setText(taskList.get(position).getT_reqnum().toString());
		holder.item_title.setText(taskList.get(position).getT_title().toString());
		holder.item_subdate.setText(taskList.get(position).getT_subdate().toString());
		return convertView;
	}
	public final class ViewHolder
	{
		public TextView item_reqnum;
	    public TextView item_title;
	    public TextView item_subdate;
	}
}
