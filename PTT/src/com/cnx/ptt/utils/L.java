package com.cnx.ptt.utils;

import android.util.Log;

/**
 * debug log class
 * 
 * 
 */
public class L {
	// debug switch
	private static boolean DEBUG = true;
	private static String TAG = "cnx.ptt.LogUtils";
	public static void i(String msg) {
		if (DEBUG)
			Log.i(TAG, msg);
	}
	//下面是传入类名打印log
	public static void i(Class<?> _class,String msg){
		if (DEBUG)
			Log.i(_class.getName(), msg);
	}
	public static void i(String TAG, String msg) {
		if (DEBUG) {
			Log.i(TAG, msg);
		}
	}
	public static void e(Class<?> _class,String msg){
		if (DEBUG)
			Log.i(_class.getName(), msg);
	}
	public static void e(String TAG, String msg) {
		if (DEBUG) {
			Log.e(TAG, msg);
		}
	}
	public static void e(String msg) {
		if (DEBUG)
			Log.e(TAG, msg);
	}
	
	
	public static void d(String msg) {
		if (DEBUG)
			Log.d(TAG, msg);
	}

	public static void d(String TAG, String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}
	public static void d(Class<?> _class,String msg){
		if (DEBUG)
			Log.d(_class.getName(), msg);
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
	public static void w(Class<?> _class,String msg){
		if (DEBUG)
			Log.w(_class.getName(), msg);
	}
	public static void w(String TAG, String msg, Throwable re) {
		if (DEBUG) {
			Log.w(TAG, msg, re);
		}
	}
	public static void w(String TAG, Throwable tr){
		if(DEBUG){
			Log.w(TAG, tr);
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
