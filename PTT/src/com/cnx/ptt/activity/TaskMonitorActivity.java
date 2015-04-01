package com.cnx.ptt.activity;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.app.ActionBar;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.cnx.ptt.Constants;
import com.cnx.ptt.R;
import com.cnx.ptt.adapter.TaskListAdapter;
import com.cnx.ptt.chat.xlistview.MsgListView;
import com.cnx.ptt.http.HttpUtil;
import com.cnx.ptt.http.Url;
import com.cnx.ptt.http.json.FollowTaskJson;
import com.cnx.ptt.http.json.TaskListItemJson;
import com.cnx.ptt.pojo.TaskListItem;
import com.cnx.ptt.utils.L;

/**
 * 当点击followIcon 关注或者取消task， 需要动态刷新列表，用taskListAdapter.notifyDataSetChanged();
 * 通知tasklist 刷新， 而不是跳转， 需要重新处理一下
 * 
 * @author IBM_ADMIN
 * 
 */
public class TaskMonitorActivity extends BaseActivity {
	
	private MsgListView listView;
	private ArrayList<TaskListItem> item_list = null;
	private TaskListAdapter taskListAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_monitor);
		listView =  (MsgListView) findViewById(R.id.task_mornitor_list);
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		TaskListAsynTask task = new TaskListAsynTask();
		task.execute();
		
		listView.setAdapter(taskListAdapter);
		listView.setOnItemClickListener(itemClickListener);
		
		registerBroadcast();//注册轮询广播
	}
	/**
	 * listview item 点击事件
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			Object obj = view.getTag();
			if (obj != null) {
				String itemId = obj.toString();
				Intent intent = new Intent(TaskMonitorActivity.this, DisplayTaskActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("itemid", itemId);
				intent.putExtras(bundle);
				startActivity(intent);
				// finish();
			}
		}
	};
	/**
	 * 注册轮询广播
	 */
	private void registerBroadcast() {
		IntentFilter pollingFilter = new IntentFilter(Constants.POLLING_FOLLOW_TASK);
		pollingFilter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(pollingReceiver, pollingFilter);
		
	}
	private BroadcastReceiver pollingReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("-------------pollingtask is run-----");
			taskListAdapter.notifyDataSetChanged();
		}
	};
	@Override
    protected void onDestroy() {
		super.onDestroy();
    	unregisterReceiver(pollingReceiver);
    	pollingReceiver = null;
    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.add_follow_task, menu);

		MenuItem search = menu.findItem(R.id.action_search_task);

		SearchView searchview = (SearchView) search.getActionView();

		SearchManager mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchableInfo info = mSearchManager
				.getSearchableInfo(getComponentName());

		searchview.setSearchableInfo(info);
		searchview.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String newText) {
				if (TextUtils.isEmpty(newText)) {
					L.i("searching", "Please input task number!");
				}
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				L.i("searching", "Search task:" + query);
				Intent intent = new Intent(TaskMonitorActivity.this,
						TaskSearchActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("query", query);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
				return false;
			}
		});
		return true;
	}

	/**
	 * AsyncTask - get user followed task list
	 * 
	 * @author David xu
	 * 
	 */
	private class TaskListAsynTask extends
			AsyncTask<Void, Void, ArrayList<TaskListItem>> {

		@Override
		protected ArrayList<TaskListItem> doInBackground(Void... params) {

			String result = null;
			String urlString = Url.TASKMORNITOR_URL;
			try {
				result = HttpUtil.getByHttpClient(
						TaskMonitorActivity.this,
						urlString,
						new BasicNameValuePair("email", String
								.valueOf(UserSession.user.getUser_email())));
			} catch (Exception e) {
				L.i("TaskMonitorActivity:result", e.getMessage());
				e.printStackTrace();
			}

			try {
				item_list = TaskListItemJson.instance(TaskMonitorActivity.this)
						.readJsonTasklistItem(result);

			} catch (Exception e) {
				L.i("TaskMonitorActivity:item_list", e.getMessage());
				e.printStackTrace();
			}
			return item_list;
		}

		@Override
		protected void onPostExecute(ArrayList<TaskListItem> result) {
			
			taskListAdapter = new TaskListAdapter(TaskMonitorActivity.this, result, false);
			

			/*
			 * listView item 长点击事件
			 * listView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					final TaskListItem item = (TaskListItem) listView
							.getItemAtPosition(position);
					L.i("TaskMonitorActivity:listView.setOnItemLongClickListene",
							item.getT_id().toString());
					AlertDialog.Builder builder = new Builder(
							TaskMonitorActivity.this);
					builder.setTitle("Note:")
							.setMessage("Do you want to unfollow this task?")
							.setNegativeButton("Cancel", null)
							.setPositiveButton("Unfollow",
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											followTask unfollow = new followTask();
											unfollow.execute(item.getT_id()
													.toString(), "unfollow");
										}
									}).setCancelable(true).show();
					return true;
				}
			});*/
		}
	}

	private class followTask extends AsyncTask<String, Void, Boolean> {
		Boolean isFollowed = true;

		@Override
		protected Boolean doInBackground(String... params) {
			String urlString = Url.TASKMORNITOR_FOLLOWED_TASK;
			String result = null;
			try {
				result = HttpUtil.getByHttpClient(
						TaskMonitorActivity.this,
						urlString,
						new BasicNameValuePair("email", String
								.valueOf(UserSession.user.getUser_email())),
						new BasicNameValuePair("taskid", params[0]),
						new BasicNameValuePair("action", params[1]));
			} catch (Exception e) {
				L.i("TaskMonitorActivity:followTask", e.getMessage());
				e.printStackTrace();
			}

			try {
				isFollowed = FollowTaskJson.instance(TaskMonitorActivity.this)
						.readJsonFollowTask(result);

			} catch (Exception e) {
				L.i("TaskMonitorActivity:item_list", e.getMessage());
				e.printStackTrace();
			}
			return isFollowed;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				showProgressDialog();
				TaskListAsynTask task = new TaskListAsynTask();
				task.execute();
			}
		}

	}
}
