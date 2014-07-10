package com.cnx.ptt.zxing;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.cnx.ptt.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

public class CaptureCodeActivity extends Activity implements
		SurfaceHolder.Callback {

	private static final String TAG = CaptureCodeActivity.class.getSimpleName();
	private boolean hasSurface;
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType, ?> decodeHints;
	private String characterSet;
	private ViewfinderView viewfinderView;
	private Result lastResult;
	private InactivityTimer inactivityTimer;

	private Result savedResultToShow;
	private boolean playBeep;
	private boolean vibrate;
	private MediaPlayer mediaPlayer;
	private static final float BEEP_VOLUME = 0.10f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_capture_code);

		this.hasSurface = false;

		inactivityTimer = new InactivityTimer(this); // complete this activity
														// in 5 mins inactive
														// status.
	}

	@Override
	protected void onResume() {
		super.onResume();

		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		setLastResult(null);
		handler = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		setVibrate(true);

		inactivityTimer.onResume();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();

		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
		}

		Intent intent = getIntent();

		if (intent != null) {
			// String action = intent.getAction();
			// String dataString = intent.getDataString();
			if (decodeFormats == null || decodeFormats.isEmpty()) {
				decodeFormats = new Vector<BarcodeFormat>();
				decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
				decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
				decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);

			}

			if (intent.hasExtra("SCAN_WIDTH") && intent.hasExtra("SCAN_HEIGHT")) {
				int width = intent.getIntExtra("SCAN_WIDTH", 0);
				int height = intent.getIntExtra("SCAN_HEIGHT", 0);
				if (width > 0 && height > 0) {
					cameraManager.setManualFramingRect(width, height);
				}
			}

			if (intent.hasExtra("SCAN_CAMERA_ID")) {
				int cameraId = intent.getIntExtra("SCAN_CAMERA_ID", -1);
				if (cameraId >= 0) {
					cameraManager.setManualCameraId(cameraId);
				}
			}
		}

	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}

	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		// ambientLightManager.stop();
		// beepManager.close();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/*
	 * Callback function for surface.callback
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	private void initCamera(SurfaceHolder surfaceHolder) {

		Log.i(TAG, "Start initCamera");

		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						decodeHints, characterSet, cameraManager);
			}
			
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}

	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler,
						R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	/*
	 * Override surface view callback function
	 * 
	 * @see
	 * android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder
	 * , int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/*
	 * Override surface view callback function
	 * 
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.
	 * SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	public Handler getHandler() {
		return this.handler;
	}

	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();
		lastResult = rawResult;
//		ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
		boolean fromLiveScan = barcode != null;
		
		if (fromLiveScan) {
			playBeepSoundAndVibrate();
			viewfinderView.drawResultBitmap(barcode, scaleFactor, rawResult);
		}
		
		handleDecodeInternally(rawResult, barcode);
	}

	private void handleDecodeInternally(Result rawResult, Bitmap barcode) {
		
		this.getIntent().putExtra("SCAN_RESULT", rawResult.getText());
		this.setResult(RESULT_OK, getIntent());
		Log.i(TAG,
				rawResult.getBarcodeFormat().toString() + ":"
						+ rawResult.getText());
		this.finish();
		return;
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}

	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public void setViewfinderView(ViewfinderView viewfinderView) {
		this.viewfinderView = viewfinderView;
	}

	public Result getLastResult() {
		return lastResult;
	}

	public void setLastResult(Result lastResult) {
		this.lastResult = lastResult;
	}

	public boolean isVibrate() {
		return vibrate;
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

}
