package com.cnx.utils;

import android.util.Log;

/**
 * debug log class
 * 
 * 
 */
public class LogUtils {
	// debug switch
	public static boolean DEBUG = true;

	public static void i(String TAG, String msg) {
		if (DEBUG) {
			Log.i(TAG, msg);
		}
	}

	public static void e(String TAG, String msg) {
		if (DEBUG) {
			Log.e(TAG, msg);
		}
	}

	public static void d(String TAG, String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}

	public static void v(String TAG, String msg) {
		if (DEBUG) {
			Log.v(TAG, msg);
		}
	}

	public static void w(String TAG, String msg) {
		if (DEBUG) {
			Log.w(TAG, msg);
		}
	}

	public static void println() {
		if (DEBUG) {
			System.out.println();
		}
	}

	public static void println(Object msg) {
		if (DEBUG) {
			System.out.println(msg);
		}
	}

	public static void print(Object msg) {
		if (DEBUG) {
			System.out.print(msg);
		}
	}

	public static void printStackTrace(Throwable e) {
		if (DEBUG) {
			e.printStackTrace();
		}
	}
}
