package com.cnx.ptt.autobahn;

import android.app.Activity;
import android.os.Handler;

public abstract class WampActivityAbstract extends Activity {
	
	public abstract Handler get_handler();
	
	public abstract void addTextMessage(String text, boolean isSend);
	
}
