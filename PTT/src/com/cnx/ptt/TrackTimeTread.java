package com.cnx.ptt;

import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;

public class TrackTimeTread extends Thread {
	private Handler handler;
	private final CountDownLatch handlerInitLatch;
	private TrackTimeActivity activity;

	public TrackTimeTread(TrackTimeActivity a) {
		handlerInitLatch = new CountDownLatch(1);
		activity = a;
	}

	public Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new TrackTimeActivityHandler(activity);
		handlerInitLatch.countDown();
		Looper.loop();
	}

}
