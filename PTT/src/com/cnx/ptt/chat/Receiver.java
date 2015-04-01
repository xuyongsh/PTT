package com.cnx.ptt.chat;

import com.cnx.ptt.Constants;
import com.cnx.ptt.autobahn.WampActivityAbstract;

public class Receiver {
	private int m_sender_id;
	private int m_receiver_id;
	private OneOneChatEvent ooc;
	private OneOneChatEventHandler ooch;
	private ChatEventOSMessage com;
	
	public Receiver(WampActivityAbstract a){
		m_sender_id = Constants.DEBUG_CLIENT_ID;
		m_receiver_id = Constants.DEBUG_TARGET_ID;
		ooc = new OneOneChatEvent();
		ooc.m_sender_id = m_sender_id;
		ooc.m_receiver_id = m_receiver_id;
		ooch = new OneOneChatEventHandler(a);
		com = new ChatEventOSMessage(ooc, ooch);
	}
	
	public void set_text_message(String txt){
		ooc.m_text_message = txt;
	}
	
	public OneOneChatEvent get_ooc(){
		return ooc;
	}
	
	public ChatEventOSMessage get_com(){
		return com;
	}
}
