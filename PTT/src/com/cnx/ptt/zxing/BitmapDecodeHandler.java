package com.cnx.ptt.zxing;

import java.util.Map;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.cnx.ptt.R;
import com.cnx.ptt.utils.L;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

public class BitmapDecodeHandler extends Handler {

	private static final String TAG = BitmapDecodeHandler.class.getSimpleName();

	private final CaptureCodeActivity activity;
	private final MultiFormatReader multiFormatReader;
	private boolean running = true;

	public BitmapDecodeHandler(CaptureCodeActivity activity,
			Map<DecodeHintType, Object> hints) {
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message) {
		if (!running) {
			return;
		}
		switch (message.what) {
		case R.id.decode:
			try {
				decode((Bitmap) message.obj);
			} catch (NotFoundException e) {

				e.printStackTrace();
			}
			break;
		case R.id.quit:
			running = false;
			Looper.myLooper().quit();
			break;
		}
	}

	private void decode(Bitmap data) throws NotFoundException {
		long start = System.currentTimeMillis();
		Result rawResult = null;

		rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(
				new HybridBinarizer(new BitmapLuminanceSource(data))));
		

		Handler handler = activity.getHandler();

		if (rawResult != null) {
			long end = System.currentTimeMillis();

			if (handler != null) {
				Message message = Message.obtain(handler,
						R.id.decode_bmp_succeeded, rawResult);
				message.sendToTarget();
			}
			L.d(TAG, "Found barcode in " + (end - start) + " ms");
		} else {
			if (handler != null) {
				Message message = Message.obtain(handler, R.id.decode_bmp_failed);
				message.sendToTarget();
			}
		}

	}

}
