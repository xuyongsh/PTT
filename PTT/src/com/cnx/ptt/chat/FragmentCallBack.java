package com.cnx.ptt.chat;

import com.cnx.ptt.activity.MessageTabActivity;
import com.cnx.ptt.chat.service.XmppService;


public interface FragmentCallBack {
	public XmppService getService();

	public MessageTabActivity getMessageActivity();
}
