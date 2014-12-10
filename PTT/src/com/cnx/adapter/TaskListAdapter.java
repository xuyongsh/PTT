package com.cnx.adapter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnx.pojo.TaskListItem;
import com.cnx.ptt.R;
import com.cnx.utils.LogUtils;

public class TaskListAdapter extends BaseAdapter{
	private Integer[] priorityImages = {R.drawable.ic_taskitem_priority_0,R.drawable.ic_taskitem_priority_1, R.drawable.ic_taskitem_priority_2, 
									R.drawable.ic_taskitem_priority_3, R.drawable.ic_taskitem_priority_4};
	//private Integer[] followImages = {R.drawable.ic_follow_add, R.drawable.ic_follow_remove};
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.task_list_item, null);
			holder = new ViewHolder();
			holder.item_reqnum = (TextView)convertView.findViewById(R.id.task_list_item_reqnum);
			holder.item_title = (TextView)convertView.findViewById(R.id.task_list_item_title);
			holder.item_subdate=(TextView)convertView.findViewById(R.id.task_list_item_subdate);
			holder.item_priority = (ImageView)convertView.findViewById(R.id.task_list_item_priority);
			/**holder.item_follow = (ImageView)convertView.findViewById(R.id.task_list_item_follow);
			
			holder.item_follow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LogUtils.d("TasklistAdapter: image onClick", ""+v.getId());
					new AlertDialog.Builder(mInflater.getContext())
					 .setTitle("Follow task") 
					 .setMessage("Do you want to follow this task").setPositiveButton("Follow", null).show();
				}
			});**/
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.item_reqnum.setText(taskList.get(position).getT_reqnum().toString());
		holder.item_title.setText(taskList.get(position).getT_title().toString());
		holder.item_subdate.setText(taskList.get(position).getT_subdate().toString());
		holder.item_priority.setImageResource(priorityImages[taskList.get(position).getT_priority().intValue()]);
		//holder.item_follow.setImageResource(followImages[taskList.get(position).getT_isFollowed().intValue()]);
		
		return convertView;
	}
	public void saveFollowed(int position){
		
	}
	public final class ViewHolder
	{
		public TextView item_reqnum;
	    public TextView item_title;
	    public TextView item_subdate;
	    public ImageView item_priority;
//	    public ImageView item_follow;
	}
}
