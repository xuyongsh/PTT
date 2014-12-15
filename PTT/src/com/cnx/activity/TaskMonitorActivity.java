package com.cnx.activity;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.cnx.activity.LoginActivity.UserSession;
import com.cnx.adapter.TaskListAdapter;
import com.cnx.http.HttpUtil;
import com.cnx.http.Url;
import com.cnx.http.json.FollowTaskJson;
import com.cnx.http.json.TaskListItemJson;
import com.cnx.pojo.TaskListItem;
import com.cnx.ptt.R;
import com.cnx.utils.LogUtils;

public class TaskMonitorActivity extends BaseActivity {
	private ListView listView;
	private ArrayList<TaskListItem> item_list = null;
	private TaskListAdapter taskListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_monitor);
		listView = (ListView) findViewById(R.id.task_mornitor_list);
		
		showProgressDialog();
		TaskListAsynTask task = new TaskListAsynTask();
		task.execute();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search_task:
			onSearchRequested();
			break;

		default:
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onSearchRequested() {
		Bundle bundle = new Bundle();
		bundle.putString("action", "follow");
		startSearch(null, false, bundle, false);
		return super.onSearchRequested();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_follow_task, menu);
		return true;
		
		
		/*MenuItem search = menu.findItem(R.id.action_add_task);

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
				Intent intent = new Intent(TaskMonitorActivity.this, FollowTaskActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("query", query);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
				return false;
			}
		});*/
		
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
								UserSession.user.getUser_id()));
			} catch (Exception e) {
				LogUtils.d("TaskMonitorActivity:result", e.getMessage());
				e.printStackTrace();
			}

			try {
				item_list = TaskListItemJson.instance(TaskMonitorActivity.this)
						.readJsonTasklistItem(result);

			} catch (Exception e) {
				LogUtils.d("TaskMonitorActivity:item_list", e.getMessage());
				e.printStackTrace();
			}
			return item_list;
		}

		@Override
		protected void onPostExecute(ArrayList<TaskListItem> result) {
			dismissProgressDialog();
			taskListAdapter = new TaskListAdapter(TaskMonitorActivity.this,
					result);
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
						finish();
					}
				}
			});
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					final TaskListItem item = (TaskListItem) listView
							.getItemAtPosition(position);
					LogUtils.d(
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
											"cancel");
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
								UserSession.user.getUser_id()),
						new BasicNameValuePair("taskid", params[0]),
						new BasicNameValuePair("action", params[1]));
			} catch (Exception e) {
				LogUtils.d("TaskMonitorActivity:followTask", e.getMessage());
				e.printStackTrace();
			}

			try {
				isFollowed = FollowTaskJson.instance(TaskMonitorActivity.this)
						.readJsonFollowTask(result);

			} catch (Exception e) {
				LogUtils.d("TaskMonitorActivity:item_list", e.getMessage());
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
