package com.cnx.ptt.zxing;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;

public class DecodeThread extends Thread {

	public static final String BARCODE_BITMAP = "barcode_bitmap";
	public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

	private final CaptureCodeActivity activity;
	private final Map<DecodeHintType, Object> hints;
	private Handler handler;
	private final CountDownLatch handlerInitLatch;

	public DecodeThread(CaptureCodeActivity captureCodeActivity,
			Collection<BarcodeFormat> decodeFormats,
			Map<DecodeHintType, ?> decodeHints, String characterSet, ViewfinderResultPointCallback viewfinderResultPointCallback) {
		this.activity = captureCodeActivity;
		handlerInitLatch = new CountDownLatch(1);

		hints = new EnumMap<>(DecodeHintType.class);
		if (decodeHints != null) {
			hints.putAll(decodeHints);
		}

		if (decodeFormats == null || decodeFormats.isEmpty()) {
			decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
//			decodeFormats.addAll(EnumSet.of(BarcodeFormat.QR_CODE));
			decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
		}

		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

		if (characterSet != null) {
			hints.put(DecodeHintType.CHARACTER_SET, characterSet);
		}

		 hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK,
				 viewfinderResultPointCallback);
		Log.i("DecodeThread", "Hints: " + hints);
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
		handler = new DecodeHandler(activity, hints);
		handlerInitLatch.countDown();
		Looper.loop();
	}

}
