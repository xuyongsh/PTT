package com.cnx.ptt.zxing;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;

public class BitmapDecodeThread extends Thread {
	
	private Handler handler;
	private final CountDownLatch handlerInitLatch;
	private final Map<DecodeHintType, Object> hints;
	private CaptureCodeActivity activity;
	
	public BitmapDecodeThread(CaptureCodeActivity activity){
		handlerInitLatch = new CountDownLatch(1);
		
		hints = new EnumMap<>(
				DecodeHintType.class);

		Collection<BarcodeFormat> decodeFormats = EnumSet
				.noneOf(BarcodeFormat.class);
		decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
		decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);

		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
		
		this.activity = activity;

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
		handler = new BitmapDecodeHandler(activity, hints);
		handlerInitLatch.countDown();
		Looper.loop();
	}
}
