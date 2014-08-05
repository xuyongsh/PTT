package com.cnx.ptt.chat;

import com.cnx.ptt.DefaultConfig;
import com.cnx.ptt.autobahn.WampActivityAbstract;

public class Receiver {
	private int m_sender_id;
	private int m_receiver_id;
	private OneOneChatEvent ooc;
	
	public Receiver(WampActivityAbstract activity){
		m_sender_id = DefaultConfig.DEBUG_CLIENT_ID;
		m_receiver_id = DefaultConfig.DEBUG_TARGET_ID;
		ooc = new OneOneChatEvent(activity);
		ooc.m_sender_id = m_sender_id;
		ooc.m_receiver_id = m_receiver_id;
	}
	
	public void set_text_message(String txt){
		ooc.m_text_message = txt;
	}
	
	public OneOneChatEvent get_ooc(){
		return ooc;
	}
}
