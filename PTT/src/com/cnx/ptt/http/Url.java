
package com.cnx.ptt.http;



public class Url {
	//Local server URL
	//public static final String URL_LOGIN = "http://10.0.2.2/track/mobile/login/login.php";
	public static final String HOST = "172.40.1.34";
	public static final String PROTOCOL = "http://";
	public static final String DOCUMENT_ROOT = Url.PROTOCOL+Url.HOST+"/track/mobile";
	public static final String LOGIN_URL = Url.DOCUMENT_ROOT+"/login/login.php";
	public static final String TASKMORNITOR_URL = Url.DOCUMENT_ROOT+"/monitor/index.php";
	public static final String TASKMORNITOR_FOLLOWED_TASK = Url.DOCUMENT_ROOT+"/monitor/follow.php";
	public static final String TASKMORNITOR_SEARCH_URL = Url.DOCUMENT_ROOT+"/monitor/search.php";
	public static final String MESSAGE_CHAT = Url.DOCUMENT_ROOT+"/chat/chatlist.php";
	
	public static final int XMPP_PORT = 5222;
	
}
