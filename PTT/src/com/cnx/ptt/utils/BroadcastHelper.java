package com.cnx.ptt.utils;

import android.content.Context;
import android.content.Intent;

public class BroadcastHelper {

	/**
	 * ����String ���͵�ֵ�Ĺ㲥
	 * @param context
	 * @param action
	 * @param key
	 * @param value
	 */
	public static void sendBroadCast(Context context,String action,String key,String value) {
		Intent intent = new Intent();
        intent.setAction(action);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(key, value);
        context.sendBroadcast(intent);
	}
	
	public static void sendBroadCast(Context context,String action,String key,int value) {
		Intent intent = new Intent();
		intent.setAction(action);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra(key, value);
		context.sendBroadcast(intent);
	}

}
