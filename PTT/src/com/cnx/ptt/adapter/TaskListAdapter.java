package com.cnx.ptt.adapter;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnx.ptt.R;
import com.cnx.ptt.activity.BaseActivity.UserSession;
import com.cnx.ptt.activity.TaskMonitorActivity;
import com.cnx.ptt.http.HttpUtil;
import com.cnx.ptt.http.Url;
import com.cnx.ptt.http.json.FollowTaskJson;
import com.cnx.ptt.pojo.TaskListItem;
import com.cnx.ptt.utils.DialogUtil;
import com.cnx.ptt.utils.L;

public class TaskListAdapter extends BaseAdapter{
	private Integer[] priorityIcon = {R.drawable.ic_taskitem_priority_0,R.drawable.ic_taskitem_priority_1, R.drawable.ic_taskitem_priority_2, 
									R.drawable.ic_taskitem_priority_3, R.drawable.ic_taskitem_priority_4};
	private Integer[] followIcon = {R.drawable.ic_follow_remove,  R.drawable.ic_follow_add};
	private LayoutInflater mInflater;  
    private ArrayList<TaskListItem> taskList;
    private boolean isFollow = true;
    private Context mContext;
    private Dialog pd;
    
    public TaskListAdapter(Context context, ArrayList<TaskListItem> list, boolean _isFollow){
    	mInflater = LayoutInflater.from(context);
    	taskList = list;
    	mContext = context;
    	isFollow = _isFollow;
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
	ViewHolder holder;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.task_list_item, null);
			holder = new ViewHolder();
			holder.item_reqnum = (TextView)convertView.findViewById(R.id.task_list_item_reqnum);
			holder.item_title = (TextView)convertView.findViewById(R.id.task_list_item_title);
			holder.item_subdate=(TextView)convertView.findViewById(R.id.task_list_item_subdate);
			holder.item_priority = (ImageView)convertView.findViewById(R.id.task_list_item_priority);
			holder.item_followIcon = (ImageView)convertView.findViewById(R.id.task_list_item_followIcon);
			
			holder.item_followIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext());
					builder.setTitle("Follow task");
					String title = "Do you want to follow this task";
					String btString = "Follow";
					if(!isFollow){
						title = "Do you want to unFollow this task";
						btString = "Unfollow";
					}
					builder.setMessage(title);
					builder.setNegativeButton("Cancel", null);
					builder.setPositiveButton(btString, new DialogInterface.OnClickListener() {
						String urlString = Url.TASKMORNITOR_FOLLOWED_TASK;
						String result = null;
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(!isFollow){//unfollow this task
								try {
									followTask follow = new followTask();
									follow.execute(taskList.get(position).getT_id().toString(),"unfollow");
								} catch (Exception e) {
									e.printStackTrace();
								}
								Toast.makeText(mInflater.getContext(), taskList.get(position).getT_reqnum().toString() + " has unfollowed", 1).show();
							}else {//follow this task
								followTask follow = new followTask();
								follow.execute(taskList.get(position).getT_id().toString(),"follow");
								
								Toast.makeText(mInflater.getContext(), taskList.get(position).getT_reqnum().toString() + " has followed", 1).show();
							}
							
						}
					});
					builder.show();
				}
			});
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.item_reqnum.setText(taskList.get(position).getT_reqnum().toString());
		holder.item_title.setText(taskList.get(position).getT_title().toString());
		holder.item_subdate.setText(taskList.get(position).getT_subdate().toString());
		
		holder.item_priority.setImageResource(priorityIcon[taskList.get(position).getT_priority().intValue()]);
		holder.item_followIcon.setImageResource(followIcon[taskList.get(position).getT_isFollowed().intValue()]);

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
	    public ImageView item_followIcon;
	}
	private class followTask extends AsyncTask<String, Void, Boolean> {
		Boolean isFollowed = true;

		@Override
		protected Boolean doInBackground(String... params) {
			String urlString = Url.TASKMORNITOR_FOLLOWED_TASK;
			String result = null;
			try {
				result = HttpUtil.getByHttpClient(mContext,
						urlString, new BasicNameValuePair("email",
								String.valueOf(UserSession.user.getUser_email())),
						new BasicNameValuePair("taskid", params[0]),
						new BasicNameValuePair("action", params[1]));
			} catch (Exception e) {
				L.d("TaskSearchActivity", e.getMessage());
				e.printStackTrace();
			}

			try {
				isFollowed = FollowTaskJson.instance(mContext)
						.readJsonFollowTask(result);

			} catch (Exception e) {
				L.d("TaskSearchActivity", e.getMessage());
				e.printStackTrace();
			}
			return isFollowed;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = DialogUtil.createLoadingDialog(mContext);
			pd.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				refreshTaskMonitor();
				pd.dismiss();
			}
		}
	}
	private void refreshTaskMonitor(){
		this.notifyDataSetChanged();
	}
}
