package com.cnx.ptt;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class TrackTimeActivityHandler extends Handler {

	private boolean running = true;

	private final TrackTimeActivity activity;

	public TrackTimeActivityHandler(TrackTimeActivity att) {
		activity = att;
	}

	@Override
	public void handleMessage(Message msg) {
		if (!running) {
			return;
		}

		switch (msg.what) {
		case R.id.tt_start:
			tt_start();
			break;
		case R.id.tt_stop:
			tt_stop();
			break;
		case R.id.quit:
			running = false;
			Looper.myLooper().quit();
			break;
		}
	}

	private void add_one_second(long ui_time) {

		long run_time = System.currentTimeMillis();

		if (run_time - ui_time < 1000L) {
			return;
		}

		long c_t = run_time;
		long s_t = activity.getStart_tracking_time();

		long l_t = (c_t - s_t) / 1000;

		int second = (int) (l_t % 60);

		String s_str = String.valueOf(second);

		if (s_str.length() <= 1) {
			s_str = "0" + s_str;
		}

		int minutes = (int) (Math.floor(l_t / 60) % 60);

		String m_str = String.valueOf(minutes);

		if (m_str.length() <= 1) {
			m_str = "0" + m_str;
		}

		int hours = (int) Math.floor(l_t / 3600);

		String h_str = String.valueOf(hours);

		if (h_str.length() <= 1) {
			h_str = "0" + h_str;
		}

		String time = h_str + ":" + m_str + ":" + s_str;

		Message update = Message.obtain(activity.get_handler(),
				R.id.tt_ui_update, time);
		update.sendToTarget();

	}

	private void tt_start() {
		long c_t = activity.getCurrent_time();
		while (activity.isTracking_started()) {
			add_one_second(c_t);
		}
	}

	private void tt_stop() {
		// call save time sheet method?

	}

}
