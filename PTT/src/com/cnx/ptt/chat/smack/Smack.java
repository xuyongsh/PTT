package com.cnx.ptt.chat.smack;

import com.cnx.ptt.chat.exception.XmppException;


public interface Smack {
	public boolean login(String account, String password) throws XmppException;

	public boolean logout();

	public boolean isAuthenticated();

	public void addRosterItem(String user, String alias, String group)
			throws XmppException;

	public void removeRosterItem(String user) throws XmppException;

	public void renameRosterItem(String user, String newName)
			throws XmppException;

	public void moveRosterItemToGroup(String user, String group)
			throws XmppException;

	public void renameRosterGroup(String group, String newGroup);

	public void requestAuthorizationForRosterItem(String user);

	public void addRosterGroup(String group);

	public void setStatusFromConfig();

	public void sendMessage(String user, String message);

	public void sendServerPing();

	public String getNameForJID(String jid);
}
