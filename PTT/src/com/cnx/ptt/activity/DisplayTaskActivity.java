package com.cnx.ptt.activity;



import com.cnx.ptt.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DisplayTaskActivity extends BaseActivity {

	protected static final int GUI_STOP_NOTIFIER = 0x108;
	protected static final int GUI_THREADING_NOTIFIER = 0x109;
	public ProgressBar pro;
	public TextView num;
	public TextView att;
	public int intCounter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_task);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		myMessageHandler = new DisplayTaskHandler(this);
		pro = (ProgressBar) findViewById(R.id.task_att_progress);
		att = (TextView) findViewById(R.id.task_attachment);
		num = (TextView) findViewById(R.id.task_att_pec);
		att.setClickable(true);
		att.setOnClickListener(new AttchClick());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_task, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_display_task,
					container, false);

			return rootView;
		}

	}

	public DisplayTaskHandler myMessageHandler;

	public void setprogress() {
		pro.setProgress(intCounter);
		num.setText(intCounter+"%");
	}

	class AttchClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			pro.setMax(100);
			pro.setProgress(0);
			num.setText("0%");
			pro.setVisibility(View.VISIBLE);
//			num.setVisibility(View.VISIBLE);
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < 10; i++) {
						try {
							intCounter = (i + 1) * 20;
							Thread.sleep(1000);
							if (i == 4) {
								Message m = new Message();
								m.what = DisplayTaskActivity.GUI_STOP_NOTIFIER;
								DisplayTaskActivity.this.myMessageHandler
										.sendMessage(m);

							} else {
								Message m = new Message();
								m.what = DisplayTaskActivity.GUI_THREADING_NOTIFIER;
								DisplayTaskActivity.this.myMessageHandler
										.sendMessage(m);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}

			}).start();

		}

	}

}

class DisplayTaskHandler extends Handler {

	private DisplayTaskActivity at;

	public DisplayTaskHandler(DisplayTaskActivity a) {
		at = a;
	}

	// @Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case DisplayTaskActivity.GUI_STOP_NOTIFIER:
			at.pro.setVisibility(View.GONE);
			at.num.setVisibility(View.GONE);
			Thread.currentThread().interrupt();
			break;
		case DisplayTaskActivity.GUI_THREADING_NOTIFIER:
			if (!Thread.currentThread().isInterrupted()) {
				at.setprogress();
			}
			break;
		}

		super.handleMessage(msg);
	}

}