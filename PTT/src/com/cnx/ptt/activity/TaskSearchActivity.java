package com.cnx.ptt.activity;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.R.string;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.cnx.ptt.R;
import com.cnx.ptt.adapter.TaskListAdapter;
import com.cnx.ptt.http.HttpUtil;
import com.cnx.ptt.http.Url;
import com.cnx.ptt.http.json.FollowTaskJson;
import com.cnx.ptt.http.json.TaskSearchJson;
import com.cnx.ptt.pojo.TaskListItem;
import com.cnx.ptt.utils.LogUtils;

public class TaskSearchActivity extends BaseActivity {
	private ListView listView;
	private ArrayList<TaskListItem> item_list = null;
	private TaskListAdapter taskListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_search);
		showinfo("onCreate is called");
		listView = (ListView) findViewById(R.id.task_list);
		showProgressDialog();
		
		SearchTask task = new SearchTask();
		task.execute(getIntent().getStringExtra("query"));
	}
	
	private void showinfo(String s) {
		LogUtils.i("---TaskSearchActivity---", s);
	}
	private void doSearchQuery(Intent intent){
		if(intent == null)
			return;
		String queryString = null;
		String queryAction = intent.getAction();
		if(Intent.ACTION_SEARCH.equals(queryAction)){
			queryString = intent.getStringExtra(SearchManager.QUERY);
		}
		showinfo(queryString);
	}
	private class SearchTask extends AsyncTask<String, Void, ArrayList<TaskListItem>>{

		@Override
		protected ArrayList<TaskListItem> doInBackground(String... params) {
			String result = null;
			String urlString = Url.TASKMORNITOR_SEARCH_URL;
			try {
				result = HttpUtil.getByHttpClient(TaskSearchActivity.this, urlString, 
						new BasicNameValuePair("userid", String.valueOf(UserSession.user.getUser_id())),
						new BasicNameValuePair("query", params[0]));
			} catch (Exception e) {
				LogUtils.d("TaskSearchActivity", e.getMessage());
				e.printStackTrace();
			}
			item_list = TaskSearchJson.instance(TaskSearchActivity.this).readJsonTaskSearch(result);
			
			return item_list;
		}
		@Override
		protected void onPostExecute(ArrayList<TaskListItem> result) {
			dismissProgressDialog();
			taskListAdapter = new TaskListAdapter(TaskSearchActivity.this, result);
			listView.setAdapter(taskListAdapter);
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					final TaskListItem item = (TaskListItem)listView.getItemAtPosition(position);
					AlertDialog.Builder builder = new AlertDialog.Builder(TaskSearchActivity.this);
					builder.setTitle("Note:")
					.setMessage("Do you want to follow this task?")
					.setNegativeButton("Cancel", null)
					.setPositiveButton("Follow", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							followTask follow = new followTask();
							follow.execute(item.getT_id().toString(),"follow");
						}
					}).setCancelable(true).show();
					return true;
				}
			});
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent(TaskSearchActivity.this,
							DisplayTaskActivity.class);
					startActivity(intent);
					//finish();
					
				}
			});
		}
		
	}

	private class followTask extends AsyncTask<String, Void, Boolean> {
		Boolean isFollowed = true;

		@Override
		protected Boolean doInBackground(String... params) {
			String urlString = Url.TASKMORNITOR_FOLLOWED_TASK;
			String result = null;
			try {
				result = HttpUtil.getByHttpClient(TaskSearchActivity.this,
						urlString, new BasicNameValuePair("userid",
								String.valueOf(UserSession.user.getUser_id())),
						new BasicNameValuePair("taskid", params[0]),
						new BasicNameValuePair("action", params[1]));
			} catch (Exception e) {
				LogUtils.d("TaskSearchActivity", e.getMessage());
				e.printStackTrace();
			}

			try {
				isFollowed = FollowTaskJson.instance(TaskSearchActivity.this)
						.readJsonFollowTask(result);

			} catch (Exception e) {
				LogUtils.d("TaskSearchActivity", e.getMessage());
				e.printStackTrace();
			}
			return isFollowed;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				new Intent(TaskSearchActivity.this, TaskMonitorActivity.class);
				finish();
			}
		}

	}
}
