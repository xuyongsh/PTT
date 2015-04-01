package com.cnx.ptt;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.cnx.ptt.utils.L;
import com.cnx.ptt.zxing.CaptureCodeActivity;

public class TrackTimeActivity extends Activity {

	public final int AT_TAG = 0;

	private ArrayList<TaskRole> task_role_list;
	private ArrayList<TaskAction> task_action_list;

	private Spinner role_spinner;
	private Spinner action_spinner;
	public final String default_start_time = "00:00:00";
	private long current_time;
	private long start_tracking_time;
	private boolean tracking_started = false;
	private Handler handler;
	private TextView m_tt_dc;
	private TrackTimeTread tt_tread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_time);
		task_role_list = new ArrayList<TaskRole>();
		task_action_list = new ArrayList<TaskAction>();

		task_role_list.add(new TaskRole("Web Builder", 1));
		task_role_list.add(new TaskRole("QA Reviewer", 2));

		task_action_list.add(new TaskAction("Web Production", 1));
		task_action_list.add(new TaskAction("QA Check", 2));

		role_spinner = (Spinner) findViewById(R.id.track_time_sp_role);
		action_spinner = (Spinner) findViewById(R.id.track_time_sp_action);

		TaskRoleSpinnerAdapter tr_adpt = new TaskRoleSpinnerAdapter(
				task_role_list, this);

		TaskActionSpinnerAdapter ta_adpt = new TaskActionSpinnerAdapter(
				task_action_list, this);

		role_spinner.setAdapter(tr_adpt);

		action_spinner.setAdapter(ta_adpt);

		m_tt_dc = (TextView) findViewById(R.id.track_time_time);

		m_tt_dc.setText(default_start_time);
		
		current_time = 0L;
		start_tracking_time = 0L;
		
		set_handler(new TrackTimeActivityUIHandler(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.track_time, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_capture_code) {
			Intent i = new Intent(this.getBaseContext(),
					CaptureCodeActivity.class);
			this.startActivityForResult(i, AT_TAG);
			return true;
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == AT_TAG) {
			if (resultCode == RESULT_OK) {
				TextView tv = (TextView) this
						.findViewById(R.id.track_time_task_id);
				tv.setText("Tracking on Task "
						+ data.getStringExtra("SCAN_RESULT"));
				L.i("TT", "RESULT_OK");
				
				start_tracking();
				
			}
		}

	}
		
	
	@Override
	protected void onDestroy(){
		quit_TT_Thread();
		super.onDestroy();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		//TODO: need to redraw the view
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		//TODO: need to save status;
	}

	public void OnStartClick(View v) {
		start_tracking();
	}
	
	private void start_tracking(){
		
		if(tt_tread != null){
			quit_TT_Thread();
		}
		
		tt_tread = new TrackTimeTread(this);
		tt_tread.start();
		
		long c_t = System.currentTimeMillis();
		setCurrent_time(c_t);
		setStart_tracking_time(c_t);
		updateTimer(default_start_time);
		setTracking_started(true);
		Message start = Message.obtain(tt_tread.getHandler(),R.id.tt_start);
		start.sendToTarget();
	}

	public void OnStopClick(View v) {
		quit_TT_Thread();
		
		Message updateUI = Message.obtain(handler,R.id.tt_ui_update, default_start_time);
		updateUI.sendToTarget();
		
		setCurrent_time(0);
		setStart_tracking_time(0);
	}
	
	private void quit_TT_Thread(){
		
		if(tt_tread == null){
			return;
		}
		
		//TODO: save time sheet record??
		setTracking_started(false);
		Message quite = Message.obtain(tt_tread.getHandler(),R.id.quit);
		quite.sendToTarget();
		try {
			tt_tread.join(500L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tt_tread = null;
		
	}

	public void updateTimer(String time) {
		m_tt_dc.setText(time);
	}

	public long getCurrent_time() {
		return current_time;
	}

	public void setCurrent_time(long l) {
		this.current_time = l;
	}

	public long getStart_tracking_time() {
		return start_tracking_time;
	}

	public void setStart_tracking_time(long start_tracking_time) {
		this.start_tracking_time = start_tracking_time;
	}

	public boolean isTracking_started() {
		return tracking_started;
	}

	public void setTracking_started(boolean tracking_started) {
		this.tracking_started = tracking_started;
	}

	public Handler get_handler() {
		return handler;
	}

	public void set_handler(Handler handler) {
		this.handler = handler;
	}
}
