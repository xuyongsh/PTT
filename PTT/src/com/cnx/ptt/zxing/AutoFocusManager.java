package com.cnx.ptt.zxing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RejectedExecutionException;

import com.cnx.ptt.utils.L;

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;

public class AutoFocusManager implements Camera.AutoFocusCallback{

	 private static final String TAG = AutoFocusManager.class.getSimpleName();

	  private static final long AUTO_FOCUS_INTERVAL_MS = 2000L;
	  private static final Collection<String> FOCUS_MODES_CALLING_AF;
	  static {
	    FOCUS_MODES_CALLING_AF = new ArrayList<>(2);
	    FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO);
	    FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO);
	  }

	  private boolean stopped;
	  private boolean focusing;
	  private final boolean useAutoFocus;
	  private final Camera camera;
	  private AsyncTask<?,?,?> outstandingTask;
	
	public AutoFocusManager(Context context, Camera camera) {
		this.camera = camera;
	    String currentFocusMode = camera.getParameters().getFocusMode();
	    useAutoFocus = FOCUS_MODES_CALLING_AF.contains(currentFocusMode);
	    L.i(TAG, "Current focus mode '" + currentFocusMode + "'; use auto focus? " + useAutoFocus);
	    start();
	}

	synchronized void start() {
	    if (useAutoFocus) {
	      outstandingTask = null;
	      if (!stopped && !focusing) {
	        try {
	          camera.autoFocus(this);
	          focusing = true;
	        } catch (RuntimeException re) {
	          // Have heard RuntimeException reported in Android 4.0.x+; continue?
	        	L.w(TAG, "Unexpected exception while focusing", re);
	          // Try again later to keep cycle going
	          autoFocusAgainLater();
	        }
	      }
	    }
	  }

	  @Override
	  public synchronized void onAutoFocus(boolean success, Camera theCamera) {
	    focusing = false;
	    autoFocusAgainLater();
	  }

	private synchronized void autoFocusAgainLater() {
    if (!stopped && outstandingTask == null) {
      AutoFocusTask newTask = new AutoFocusTask();
      try {
        newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        outstandingTask = newTask;
      } catch (RejectedExecutionException ree) {
    	  L.w(TAG, "Could not request auto focus", ree);
      }
    }
  }
	private synchronized void cancelOutstandingTask() {
	    if (outstandingTask != null) {
	      if (outstandingTask.getStatus() != AsyncTask.Status.FINISHED) {
	        outstandingTask.cancel(true);
	      }
	      outstandingTask = null;
	    }
	  }

	  synchronized void stop() {
	    stopped = true;
	    if (useAutoFocus) {
	      cancelOutstandingTask();
	      // Doesn't hurt to call this even if not focusing
	      try {
	        camera.cancelAutoFocus();
	      } catch (RuntimeException re) {
	        // Have heard RuntimeException reported in Android 4.0.x+; continue?
	    	  L.w(TAG, "Unexpected exception while cancelling focusing", re);
	      }
	    }
	  }
	private final class AutoFocusTask extends AsyncTask<Object,Object,Object> {
	    @Override
	    protected Object doInBackground(Object... voids) {
	      try {
	        Thread.sleep(AUTO_FOCUS_INTERVAL_MS);
	      } catch (InterruptedException e) {
	        // continue
	      }
	      start();
	      return null;
	    }
	  }

}
