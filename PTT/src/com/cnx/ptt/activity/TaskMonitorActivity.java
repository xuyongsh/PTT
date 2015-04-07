package com.cnx.ptt.activity;

import java.util.List;

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
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.cnx.ptt.Constants;
import com.cnx.ptt.R;
import com.cnx.ptt.adapter.TaskListAdapter;
import com.cnx.ptt.chat.xlistview.MsgListView;
import com.cnx.ptt.chat.xlistview.MsgListView.IXListViewListener;
import com.cnx.ptt.http.HttpUtil;
import com.cnx.ptt.http.Url;
import com.cnx.ptt.http.json.TaskListItemJson;
import com.cnx.ptt.pojo.TaskListItem;
import com.cnx.ptt.utils.L;

/**
 * �����followIcon ��ע����ȡ��task�� ��Ҫ��̬ˢ���б���taskListAdapter.notifyDataSetChanged();
 * ֪ͨtasklist ˢ�£� ��������ת�� ��Ҫ���´���һ��
 * 
 * @author IBM_ADMIN
 * 
 */
public class TaskMonitorActivity extends BaseActivity implements IXListViewListener{
	private MsgListView listView;
	private List<TaskListItem> item_list = null;
	private TaskListAdapter taskListAdapter;
	private View task_list_null;
	private Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_monitor);
		initView(); 
		
		initData();
		
		registerBroadcast();//ע����ѯ�㲥
	}
	/**
	 * �첽�����ȡtasklist ����
	 */
	private void initData() {
		TaskListAsynTask task = new TaskListAsynTask();
		task.execute();
	}
	/**
	 * ��ʼ��view
	 */
	private void initView() {
		listView = (MsgListView) findViewById(R.id.task_mornitor_list);
		listView.setPullLoadEnable(false);// �������ظ���
		listView.setPullRefreshEnable(true);// ��������ˢ��
		listView.setXListViewListener(this);// ��Ӽ��ظ��������ˢ�µļ�����������Ȼ��ʵ������ӿ�
		
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		task_list_null = findViewById(R.id.warning_task_list_null);
		
	}
	/**
	 * ע����ѯ�㲥
	 */
	private void registerBroadcast() {
		IntentFilter pollingFilter = new IntentFilter(Constants.POLLING_FOLLOW_TASK);
		pollingFilter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(pollingReceiver, pollingFilter);
		
	}
	private BroadcastReceiver pollingReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(Constants.POLLING_FOLLOW_TASK)){
				initData();
				L.i("---------- polling receiver------");
			}
		}
	};
	 @Override
    protected void onDestroy() {
    	super.onDestroy();
    	unregisterReceiver(pollingReceiver);
    	pollingReceiver = null;
    }
	/**
	 * AsyncTask - get user followed task list
	 * 
	 * @author David xu
	 * 
	 */
	private class TaskListAsynTask extends
			AsyncTask<Void, Void, List<TaskListItem>> {
		@Override
		protected void onPreExecute() {
			showProgressDialog();
		}
		@Override
		protected List<TaskListItem> doInBackground(Void... params) {

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
				item_list = TaskListItemJson.instance(TaskMonitorActivity.this).readJsonTasklistItem(result);

			} catch (Exception e) {
				L.i("TaskMonitorActivity:item_list", e.getMessage());
				e.printStackTrace();
			}
			return item_list;
		}

		@Override
		protected void onPostExecute(List<TaskListItem> result) {
			
			dismissProgressDialog();
			
			taskListAdapter = new TaskListAdapter(TaskMonitorActivity.this, result, false);
			if(result.size() > 0){
				task_list_null.setVisibility(View.GONE);
			}else {
				task_list_null.setVisibility(View.VISIBLE);
			}
			listView.setAdapter(taskListAdapter);
			
			listView.setOnItemClickListener(new OnItemClickListener() {

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
			});
		}
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
	@Override
	public void onRefresh() {
		
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				initData();
				listView.stopRefresh();//������ˢ�µ�ͼƬ��ʧ
			}
		};
		handler.postDelayed(r , 3000);
		
	}
	@Override
	public void onLoadMore() {
		Toast.makeText(this, "onLoadMore", 0).show();
		
	}
}
