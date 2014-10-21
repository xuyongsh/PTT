package com.cnx.ptt;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cnx.ptt.autobahn.WampActivityAbstract;
import com.cnx.ptt.autobahn.WampThread;
import com.cnx.ptt.chat.ChatEventOSMessage;
import com.cnx.ptt.chat.OneOneChatEvent;
import com.cnx.ptt.chat.OneOneChatEventHandler;
import com.cnx.ptt.chat.Receiver;

public class DisplayMessageActivity extends WampActivityAbstract {

	// private static final String TAG = DisplayMessageActivity.class.getName();
	private LinearLayout lo;
	private ScrollView scroll;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_display_message);

		String[] TITLE = { "WPC PTT Team", "Theo Wang", "Lois Teo" };
		int id = (int) getIntent().getLongExtra("c_item_id", 1);

		super.setTitle(TITLE[id]);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		handler = new DisplayMessageHandler(this);
		// Get the message from the intent
		// Intent intent = getIntent();

		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// setContentView(R.layout.custom_title);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// "Theo Wang");
	}

	@Override
	protected void onResume() {
		super.onResume();

		lo = (LinearLayout) findViewById(R.id.lv_display_message);
		scroll = (ScrollView) findViewById(R.id.sv_display_message_scroll);
		OneOneChatEvent ooc = new OneOneChatEvent();
		ooc.m_sender_id = DefaultConfig.DEBUG_CLIENT_ID;
		ooc.m_receiver_id = DefaultConfig.DEBUG_TARGET_ID;

		OneOneChatEventHandler ooch = new OneOneChatEventHandler(this);

		ChatEventOSMessage com = new ChatEventOSMessage(ooc, ooch);

		Message m = Message.obtain(WampThread.obtain().getHandler(),
				R.id.wamp_subscribe_ooc, com);
		m.sendToTarget();
	}

	@Override
	protected void onDestroy() {
		Message.obtain(this.get_handler(), R.id.quit);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_display_message,
					container, false);
			return rootView;
		}
	}

	@Override
	public Handler get_handler() {
		return handler;
	}

	public void addTextMessage(String text, boolean isSend) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView tv = (TextView) inflater.inflate(
				R.layout.display_message_item, null);
		tv.setText(text);
		lo.addView(tv);
		if (!isSend) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.RIGHT;
			tv.setLayoutParams(lp);
		}

		Message.obtain(this.handler, R.id.displaymessage_scroll_to_bottom)
				.sendToTarget();
	}

	public void mScrollToBottom() {
		int off = lo.getMeasuredHeight() - scroll.getHeight();
		if (off > 0) {
			scroll.scrollTo(0, off);
		}

	}

	public void SendNowOnClick(View v) {
		EditText et = (EditText) findViewById(R.id.input_message);

		String content = et.getText().toString();

		if (content.length() == 0) {
			return;
		}

		Receiver rec = new Receiver(this);
		rec.set_text_message(content);

		WampThread wt = WampThread.obtain();

		Message ms = Message.obtain(wt.getHandler(), R.id.wamp_publish_ooc,
				rec.get_com());
		ms.sendToTarget();

		addTextMessage(content, true);

		et.setText("");
	}

}
