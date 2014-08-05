package com.cnx.ptt;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
/**
 * This is used to update display message list
 * @author Theo
 *
 */
public class DisplayMessageHandler extends Handler {
	
	private String TAG = DisplayMessageHandler.class.getName();
	private DisplayMessageActivity activity;
	
	public DisplayMessageHandler(DisplayMessageActivity a){
		activity = a;
	}
	
	@Override
	public void handleMessage(Message msg){
		
		switch(msg.what){
		case R.id.displaymessage_new_txt_message:
			NewMessage((String) msg.obj);
			break;
		case R.id.displaymessage_send_txt_message:
			SendMessage((String) msg.obj);
			break;
		case R.id.displaymessage_scroll_to_bottom:
			this.activity.mScrollToBottom();
			break;
		case R.id.quit:
			Looper.myLooper().quit();
			break;
		}
		
	}
	
	private void NewMessage(String text){
		this.activity.addTextMessage(text, false);
		Log.d(TAG, "New message coming:"+text);
	}
	
	private void SendMessage(String text){
		this.activity.addTextMessage(text, true);
		Log.d(TAG, "New message send"+text);
	}
}
