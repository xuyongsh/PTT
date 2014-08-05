package com.cnx.ptt.chat;

import java.util.Date;

import de.tavendo.autobahn.Wamp;

public abstract class ChatEvent {
	public int num;
	public boolean flag;
    public Date created;
    public double rand;
    
    public abstract Wamp.EventHandler getHandler();

}
