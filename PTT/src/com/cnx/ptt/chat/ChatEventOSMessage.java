package com.cnx.ptt.chat;

public class ChatEventOSMessage {
	
	private OneOneChatEvent ooc;
	private OneOneChatEventHandler ooch;
	
	public ChatEventOSMessage(OneOneChatEvent oc, OneOneChatEventHandler oh){
		this.setOoc(oc);
		this.setOoch(oh);
	}

	public OneOneChatEvent getOoc() {
		return ooc;
	}

	public void setOoc(OneOneChatEvent ooc) {
		this.ooc = ooc;
	}

	public OneOneChatEventHandler getOoch() {
		return ooch;
	}

	public void setOoch(OneOneChatEventHandler ooch) {
		this.ooch = ooch;
	}

}
