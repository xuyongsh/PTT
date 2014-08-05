package com.cnx.ptt;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class TrackTimeActivityUIHandler extends Handler {
	
	private TrackTimeActivity activity;
	private boolean running = true;

	public TrackTimeActivityUIHandler(TrackTimeActivity trackTimeActivity) {
		activity = trackTimeActivity;
	}
	
	@Override
	public void handleMessage(Message msg){
		
		if (!running) {
			return;
		}
		
		switch(msg.what){
		case R.id.tt_ui_update:
			update_ui_time((String) msg.obj);
			break;
		case R.id.quit:
			running = false;
			Looper.myLooper().quit();
			break;
		}
	}
	
	private void update_ui_time(String time){
		activity.updateTimer(time);
	}

	
}
