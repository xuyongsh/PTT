package com.cnx.ptt.zxing;

import android.app.Activity;
import android.content.DialogInterface;

public class FinishListener implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {

	private final Activity activityToFinish;
	
	public FinishListener(CaptureCodeActivity captureCodeActivity) {
		this.activityToFinish = captureCodeActivity;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		 run();

	}

	@Override
	public void onCancel(DialogInterface dialog) {
		 run();
		
	}
	
	private void run() {
	    activityToFinish.finish();
	  }

}
