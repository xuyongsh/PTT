package com.cnx.ptt.autobahn;

import android.util.Log;

import com.cnx.ptt.Constants;
import com.cnx.ptt.utils.L;

import de.tavendo.autobahn.Wamp;
import de.tavendo.autobahn.WampConnection;

/**
 * This is used to receive messages, need to call display message handler..
 * 
 * @author Theo
 * 
 */
public class WampHandler implements Wamp.ConnectionHandler {

	private String TAG = WampHandler.class.getName();
	private WampConnection mConnection;
	private WampThread thread;

	public WampHandler(WampThread thread, WampConnection c) {
		this.thread = thread;
		mConnection = c;
	}
	
	private static class Event1{
		public int counter;
		public String msg;
	}

	@Override
	public void onOpen() {
		L.d(TAG, "Status: Connected to" + thread.wsuri);
		// mConnection.prefix(DefaultConfig.WAMP_EVENT_PREFIX,
		// DefaultConfig.WAMP_EVENT_HTTP_URI);
		// Message m = Message.obtain(thread.getHandler(),
		// R.id.wamp_rpc_ooc_unread);
		// m.sendToTarget();
		// TODO: Build global data layer, and update UI in data layer
		// Subscribe
		mConnection.subscribe(Constants.TEST_EVENT, Event1.class,
				new Wamp.EventHandler() {

					@Override
					public void onEvent(String topicUri, Object event) {

						// when we get an event, we safely can cast to the type
						// we specified previously
						Event1 evt = (Event1) event;

						L.d(TAG, "event received" + evt.msg + evt.counter);
					}
				});
	}

	@Override
	public void onClose(int code, String reason) {
		L.d(TAG, "Connection lost.");
	}

}
