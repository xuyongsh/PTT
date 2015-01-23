package com.cnx.ptt.activity;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.cnx.ptt.R;
import com.cnx.ptt.adapter.TaskListAdapter;
import com.cnx.ptt.http.HttpUtil;
import com.cnx.ptt.http.Url;
import com.cnx.ptt.http.json.FollowTaskJson;
import com.cnx.ptt.http.json.TaskListItemJson;
import com.cnx.ptt.pojo.TaskListItem;
import com.cnx.ptt.utils.LogUtils;
/**
 * 当点击followIcon 关注或者取消task，  需要动态刷新列表，用taskListAdapter.notifyDataSetChanged(); 
 * 通知tasklist 刷新， 而不是跳转， 需要重新处理一下
 * @author IBM_ADMIN
 *
 */
public class TaskMonitorActivity extends BaseActivity {
	private ListView listView;
	private ArrayList<TaskListItem> item_list = null;
	private TaskListAdapter taskListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_monitor);
		listView = (ListView) findViewById(R.id.task_mornitor_list);
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		showProgressDialog();
		TaskListAsynTask task = new TaskListAsynTask();
		task.execute();
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
				if(TextUtils.isEmpty(newText)){
					LogUtils.i("searching", "Please input task number!");
				}
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				LogUtils.i("searching", "Search task:"+query);
				Intent intent = new Intent(TaskMonitorActivity.this, TaskSearchActivity.class);
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
				result = HttpUtil.getByHttpClient(TaskMonitorActivity.this,
						urlString, new BasicNameValuePair("userid",
								String.valueOf(UserSession.user.getUser_id())));
			} catch (Exception e) {
				LogUtils.i("TaskMonitorActivity:result", e.getMessage());
				e.printStackTrace();
			}

			try {
				item_list = TaskListItemJson.instance(TaskMonitorActivity.this)
						.readJsonTasklistItem(result);

			} catch (Exception e) {
				LogUtils.i("TaskMonitorActivity:item_list", e.getMessage());
				e.printStackTrace();
			}
			return item_list;
		}

		@Override
		protected void onPostExecute(ArrayList<TaskListItem> result) {
			dismissProgressDialog();
			taskListAdapter = new TaskListAdapter(TaskMonitorActivity.this, result, false);
			listView.setAdapter(taskListAdapter);
			
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Object obj = view.getTag();
					if (obj != null) {
						String itemId = obj.toString();
						Intent intent = new Intent(TaskMonitorActivity.this,
								DisplayTaskActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("itemid", itemId);
						intent.putExtras(bundle);
						startActivity(intent);
						//finish();
					}
				}
			});
			
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					final TaskListItem item = (TaskListItem) listView
							.getItemAtPosition(position);
					LogUtils.i(
							"TaskMonitorActivity:listView.setOnItemLongClickListene",
							item.getT_id().toString());
					AlertDialog.Builder builder = new Builder(
							TaskMonitorActivity.this);
					builder.setTitle("Note:")
					.setMessage("Do you want to unfollow this task?")
					.setNegativeButton("Cancel", null)
					.setPositiveButton("Unfollow",
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									followTask unfollow = new followTask();
									unfollow.execute(item.getT_id().toString(),
											"unfollow");
								}
							})
					.setCancelable(true).show();
					return true;
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
				result = HttpUtil.getByHttpClient(TaskMonitorActivity.this,
						urlString, new BasicNameValuePair("userid",
								String.valueOf(UserSession.user.getUser_id())),
						new BasicNameValuePair("taskid", params[0]),
						new BasicNameValuePair("action", params[1]));
			} catch (Exception e) {
				LogUtils.i("TaskMonitorActivity:followTask", e.getMessage());
				e.printStackTrace();
			}

			try {
				isFollowed = FollowTaskJson.instance(TaskMonitorActivity.this)
						.readJsonFollowTask(result);

			} catch (Exception e) {
				LogUtils.i("TaskMonitorActivity:item_list", e.getMessage());
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
