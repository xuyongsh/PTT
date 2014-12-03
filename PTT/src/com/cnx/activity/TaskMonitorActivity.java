package com.cnx.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.cnx.activity.LoginActivity.UserSession;
import com.cnx.adapter.TaskListAdapter;
import com.cnx.http.HttpUtil;
import com.cnx.http.Url;
import com.cnx.http.json.TaskListItemJson;
import com.cnx.pojo.TaskListItem;
import com.cnx.ptt.R;

public class TaskMonitorActivity extends Activity {
	private ListView listView;
	private ArrayList<TaskListItem> item_list = null;
	private TaskListAdapter taskListAdapter;
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_monitor);
		listView = (ListView)findViewById(R.id.task_mornitor_list);
		progressDialog.show();
		TaskListAsynTask task = new TaskListAsynTask();
		task.execute();
		
	}
	
	private class TaskListAsynTask extends AsyncTask<Void, Void, ArrayList<TaskListItem>> {

		@Override
		protected ArrayList<TaskListItem> doInBackground(Void... params) {

			String result = null;
			String urlString = Url.TASKMORNITOR_URL;
			try {
				result = HttpUtil.getByHttpClient(TaskMonitorActivity.this,
						urlString, new BasicNameValuePair("userid",
								UserSession.user.getUser_id()));
			} catch (UnsupportedEncodingException e) {
				
				e.printStackTrace();
			} catch (Exception e) {

				e.printStackTrace();
			}

			try {
				item_list = TaskListItemJson.instance(TaskMonitorActivity.this).readJsonTasklistItem(result);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return item_list;
		}

		@Override
		protected void onPostExecute(ArrayList<TaskListItem> result) {
			taskListAdapter = new TaskListAdapter(TaskMonitorActivity.this, result); 
			listView.setAdapter(taskListAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
			
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Object obj = view.getTag();
					if(obj!=null){
						String itemId = obj.toString();
		                   Intent intent = new Intent(TaskMonitorActivity.this,DisplayTaskActivity.class);
		                   Bundle bundle = new Bundle();
		                   bundle.putString("itemid", itemId);
		                   intent.putExtras(bundle);
		                   startActivity(intent);
		                   finish();
					}
					
				}
			});
		}

	}
}
