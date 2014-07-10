package com.cnx.ptt.zxing;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class PreviewCallback implements Camera.PreviewCallback {
	private static final String TAG = PreviewCallback.class.getSimpleName();

	  private final CameraConfigurationManager configManager;
	  private Handler previewHandler;
	  private int previewMessage;
	  
	public PreviewCallback(CameraConfigurationManager configManager) {
		this.configManager = configManager;
	}

	public void setHandler(Handler handler, int message) {
		this.previewHandler = handler;
	    this.previewMessage = message;
		
	}
	
	 @Override
	  public void onPreviewFrame(byte[] data, Camera camera) {
	    Point cameraResolution = configManager.getCameraResolution();
	    Handler thePreviewHandler = previewHandler;
	    if (cameraResolution != null && thePreviewHandler != null) {
	      Message message = thePreviewHandler.obtainMessage(previewMessage, cameraResolution.x,
	          cameraResolution.y, data);
	      message.sendToTarget();
	      previewHandler = null;
	    } else {
	      Log.d(TAG, "Got preview callback, but no handler or resolution available");
	    }
	  }

}
