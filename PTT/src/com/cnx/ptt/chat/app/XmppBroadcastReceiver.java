package com.cnx.ptt.chat.app;

import java.util.ArrayList;

import com.cnx.ptt.chat.service.XmppService;
import com.cnx.ptt.chat.utils.PreferenceConstants;
import com.cnx.ptt.chat.utils.PreferenceUtils;
import com.cnx.ptt.utils.L;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

public class XmppBroadcastReceiver extends BroadcastReceiver {
	public static final String BOOT_COMPLETED_ACTION = "com.cnx.ptt.action.BOOT_COMPLETED";
	public static ArrayList<EventHandler> mListeners = new ArrayList<EventHandler>();
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		L.i("action = " + action);
		if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
			if (mListeners.size() > 0)// 通知接口完成加载
				for (EventHandler handler : mListeners) {
					handler.onNetChange();
				}
		} else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
			L.d("System shutdown, stopping service.");
			Intent xmppServiceIntent = new Intent(context, XmppService.class);
			context.stopService(xmppServiceIntent);
		} else {
			//将开机自动启动关闭 false
			if (!TextUtils.isEmpty(PreferenceUtils.getPrefString(context,PreferenceConstants.PASSWORD, ""))&& PreferenceUtils.getPrefBoolean(context,
							PreferenceConstants.AUTO_START, false)) {
				L.d("System work, starting service.");
				Intent i = new Intent(context, XmppService.class);
				i.setAction(BOOT_COMPLETED_ACTION);
				context.startService(i);
			}
		}
	}

	public static abstract interface EventHandler {

		public abstract void onNetChange();
	}

}
