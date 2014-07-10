package com.cnx.ptt.zxing;

import android.content.SharedPreferences;

public enum FrontLightMode {

	  /** Always on. */
	  ON,
	  /** On only when ambient light is low. */
	  AUTO,
	  /** Always off. */
	  OFF;

	  private static FrontLightMode parse(String modeString) {
	    return modeString == null ? OFF : valueOf(modeString);
	  }

	  public static FrontLightMode readPref(SharedPreferences sharedPrefs) {
	    return parse(sharedPrefs.getString("preferences_front_light_mode", OFF.toString()));
	  }

	}
