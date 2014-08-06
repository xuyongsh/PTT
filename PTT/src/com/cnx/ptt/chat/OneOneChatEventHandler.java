package com.cnx.ptt.chat;

import android.os.Message;
import android.util.Log;

import com.cnx.ptt.R;
import com.cnx.ptt.autobahn.WampActivityAbstract;

import de.tavendo.autobahn.Wamp;

public class OneOneChatEventHandler implements Wamp.EventHandler {

	private WampActivityAbstract m_activity;

	public OneOneChatEventHandler(WampActivityAbstract activity) {
		this.setActivity(activity);
	}

	private void setActivity(WampActivityAbstract activity) {
		m_activity = activity;
		
	}

	@Override
	public void onEvent(String arg0, Object arg1) {
		OneOneChatEvent ooc = (OneOneChatEvent) arg1;
		Message m = Message.obtain(this.getActivity().get_handler(),
				R.id.displaymessage_new_txt_message, ooc.m_text_message);
		m.sendToTarget();
		Log.d("OneOneChatEventHandler", ooc.m_text_message);
	}

	private WampActivityAbstract getActivity() {
		// TODO Auto-generated method stub
		return m_activity;
	}

}
