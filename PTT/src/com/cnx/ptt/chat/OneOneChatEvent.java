package com.cnx.ptt.chat;

import com.cnx.ptt.autobahn.WampActivityAbstract;

import de.tavendo.autobahn.Wamp.EventHandler;

public class OneOneChatEvent extends ChatEvent {
	public int m_sender_id;
	public int m_receiver_id;
	public String m_text_message;
	private EventHandler handler;
	
	public OneOneChatEvent(WampActivityAbstract activity){
		handler = new OneOneChatEventHandler(activity);
	}

	@Override
	public String toString() {

		return "{sender: " + m_sender_id + ", receiver =" + m_receiver_id
				+ ", message =" + m_text_message
				+ ", created: " + created + ", num: " + num + ", rand: " + rand
				+ ", flag:" + flag + "}";

	}

	@Override
	public EventHandler getHandler() {
		return handler;
	}

}
