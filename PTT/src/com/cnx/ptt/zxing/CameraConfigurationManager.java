package com.cnx.ptt.zxing;

import com.google.zxing.client.android.camera.CameraConfigurationUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class CameraConfigurationManager {

	private static final String TAG = "CameraConfiguration";

	private final Context context;
	private Point screenResolution;
	private Point cameraResolution;

	public CameraConfigurationManager(Context context) {
		this.context = context;
	}

	public void initFromCameraParameters(Camera theCamera) {
		Camera.Parameters parameters = theCamera.getParameters();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point theScreenResolution = new Point();
		display.getSize(theScreenResolution);
		screenResolution = theScreenResolution;
		Log.i(TAG, "Screen resolution: " + screenResolution);
		cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(
				parameters, screenResolution);
		Log.i(TAG, "Camera resolution: " + cameraResolution);

	}

	public void setDesiredCameraParameters(Camera camera, boolean safeMode) {
		Camera.Parameters parameters = camera.getParameters();

		if (parameters == null) {
			Log.w(TAG,
					"Device error: no camera parameters are available. Proceeding without configuration.");
			return;
		}

		Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

		if (safeMode) {
			Log.w(TAG,
					"In camera config safe mode -- most settings will not be honored");
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		initializeTorch(parameters, prefs, safeMode);

		CameraConfigurationUtils.setFocus(parameters,
				prefs.getBoolean("preferences_auto_focus", true),
				prefs.getBoolean("preferences_disable_continuous_focus", true),
				safeMode);

		if (!safeMode) {
			if (prefs.getBoolean("preferences_invert_scan", false)) {
				CameraConfigurationUtils.setInvertColor(parameters);
			}

			if (!prefs.getBoolean("preferences_disable_barcode_scene_mode",
					true)) {
				CameraConfigurationUtils.setBarcodeSceneMode(parameters);
			}

			if (!prefs.getBoolean("preferences_disable_metering", true)) {
				CameraConfigurationUtils.setVideoStabilization(parameters);
				CameraConfigurationUtils.setFocusArea(parameters);
				CameraConfigurationUtils.setMetering(parameters);
			}

		}

		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		camera.setParameters(parameters);

		Camera.Parameters afterParameters = camera.getParameters();
		Camera.Size afterSize = afterParameters.getPreviewSize();
		if (afterSize != null
				&& (cameraResolution.x != afterSize.width || cameraResolution.y != afterSize.height)) {
			Log.w(TAG, "Camera said it supported preview size "
					+ cameraResolution.x + 'x' + cameraResolution.y
					+ ", but after setting it, preview size is "
					+ afterSize.width + 'x' + afterSize.height);
			cameraResolution.x = afterSize.width;
			cameraResolution.y = afterSize.height;
		}

	}

	private void initializeTorch(Parameters parameters,
			SharedPreferences prefs, boolean safeMode) {
		boolean currentSetting = FrontLightMode.readPref(prefs) == FrontLightMode.ON;
		doSetTorch(parameters, currentSetting, safeMode);

	}

	private void doSetTorch(Camera.Parameters parameters, boolean newSetting,
			boolean safeMode) {
		CameraConfigurationUtils.setTorch(parameters, newSetting);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (!safeMode
				&& !prefs.getBoolean("preferences_disable_exposure", true)) {
			CameraConfigurationUtils.setBestExposure(parameters, newSetting);
		}

	}

	public Point getScreenResolution() {
		return screenResolution;
	}

	public Point getCameraResolution() {
		return cameraResolution;
	}

}
