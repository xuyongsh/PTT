package com.cnx.ptt.autobahn;

import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;

import com.cnx.ptt.Constants;

import de.tavendo.autobahn.Wamp.ConnectionHandler;
import de.tavendo.autobahn.WampConnection;

/**
 * This is a Thread to control Wamp activity globally. There is only one
 * instance in entire app.
 * 
 * @author Theo
 * 
 */
public class WampThread extends Thread {

	private static WampThread thread = null;

	private ConnectionHandler wamphandler;
	private Handler wthandler;
	private final CountDownLatch handlerInitLatch;
	final String wsuri = Constants.WS_URI;
	private final WampConnection mConnection;

	private WampThread() {
		handlerInitLatch = new CountDownLatch(1);
		mConnection = new WampConnection();
	}

	public static WampThread obtain() {

		if (thread != null) {
			if (!thread.isAlive()) {
				thread.start();
			}
			return thread;
		}

		WampThread.thread = new WampThread();
		thread.start();
		return thread;
	}

	public ConnectionHandler getWampHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return wamphandler;
	}

	public Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return wthandler;
	}

	@Override
	public void run() {
		Looper.prepare();
		wamphandler = new WampHandler(this, mConnection);
		wthandler = new WampThreadHandler(mConnection);

		mConnection.connect(wsuri, wamphandler);

		handlerInitLatch.countDown();
		Looper.loop();
	}
}
