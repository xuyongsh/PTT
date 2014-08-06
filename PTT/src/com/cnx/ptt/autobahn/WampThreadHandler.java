package com.cnx.ptt.autobahn;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.cnx.ptt.DefaultConfig;
import com.cnx.ptt.R;
import com.cnx.ptt.chat.ChatEventOSMessage;
import com.cnx.ptt.chat.OneOneChatEvent;
import com.cnx.ptt.chat.OneOneChatEventHandler;

import de.tavendo.autobahn.WampConnection;

/**
 * This handler is used to control the instance of WampThread.
 * @author Theo
 *
 */

public class WampThreadHandler extends Handler {

	private String TAG = WampThreadHandler.class.getName();
	private WampConnection mConnection;
	private boolean running = true;
	private OneOneChatEvent ooc;
	private OneOneChatEventHandler ooch;
	private String topic;
	private String event_prefix = DefaultConfig.WAMP_EVENT_HTTP_URI+"/";

	public WampThreadHandler(WampConnection c) {
		mConnection = c;
	}

	@Override
	public void handleMessage(Message msg) {
		if (!running) {
			return;
		}
		
		if(!mConnection.isConnected()){
			return;
		}

		switch (msg.what) {
		case R.id.wamp_publish_ooc:
			this.gettopic(msg.obj);
			publish_ooc(topic, ooc);
			Log.d(TAG, "PublishTopic");
			break;
		case R.id.wamp_subscribe_ooc:
			this.gettopic_subscribe(msg.obj);
			this.subscribe_ooc(topic, ooc);
			Log.d(TAG, "SubcribeTopic");
			break;
		case R.id.wamp_rpc_ooc_unread:
			//TODO: start a rpc to load all unread connect or reconnect
			break;
		case R.id.quit:
			running = false;
			Log.d(TAG, "Quite");
			Looper.myLooper().quit();
			break;
		}
	}
	
	private void subscribe_ooc(String topic, OneOneChatEvent event){
		
		this.mConnection.subscribe(event_prefix+topic, OneOneChatEvent.class, ooch);
	}
	
	private void publish_ooc(String topic, Object event){
		this.mConnection.publish(event_prefix+topic, event);
	}
	
	private void getooc(Object o){
		if(o instanceof ChatEventOSMessage){
			ChatEventOSMessage osm = (ChatEventOSMessage) o;
			this.ooc = osm.getOoc();
			this.ooch = osm.getOoch();
			return;
		}
		this.ooc = null;
	}
	
	private void gettopic(Object o){
		if(o instanceof ChatEventOSMessage){
			this.getooc(o);
			this.topic = ooc.m_sender_id + "_" + ooc.m_receiver_id;
		}
	}
	
	private void gettopic_subscribe(Object o){
		if(o instanceof ChatEventOSMessage){
			this.getooc(o);
			this.topic = this.ooc.m_receiver_id + "_" + this.ooc.m_sender_id;
		}
	}
}
