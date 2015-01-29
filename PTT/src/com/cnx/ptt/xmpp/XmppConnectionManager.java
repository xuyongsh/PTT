package com.cnx.ptt.xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.cnx.ptt.http.Url;


/**
 * 
 * XMPP service connection tool
 * 
 */
public class XmppConnectionManager {
	private static XMPPConnection con = null;
	
	private static void openConnection() {
		try {
			ConnectionConfiguration connConfig = new ConnectionConfiguration(Url.XMPP_HOST, Url.XMPP_PORT);
			connConfig.setReconnectionAllowed(true);      
			connConfig.setCompressionEnabled(false);
			connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
			connConfig.setSASLAuthenticationEnabled(false);
			
			con = new XMPPConnection(connConfig);
			con.connect();
		} catch (XMPPException xe) {
			xe.printStackTrace();
		}
	}

	public static XMPPConnection getConnection() {
		if (con == null) {
			openConnection();
		}
		return con;
	}

	public static void closeConnection() {
		con.disconnect();
		con = null;
	}
}
