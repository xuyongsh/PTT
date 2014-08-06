package com.cnx.ptt.chat;

import org.codehaus.jackson.annotate.JsonProperty;

public class OneOneChatEvent extends ChatEvent {
	@JsonProperty
	public int m_sender_id;
	@JsonProperty
	public int m_receiver_id;
	@JsonProperty
	public String m_text_message;
	
	public OneOneChatEvent(){
		
	}

	@Override
	public String toString() {

		return "{sender: " + m_sender_id + ", receiver =" + m_receiver_id
				+ ", message =" + m_text_message
				+ ", created: " + created + ", num: " + num + ", rand: " + rand
				+ ", flag:" + flag + "}";

	}

}
