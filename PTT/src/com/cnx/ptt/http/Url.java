
package com.cnx.ptt.http;



public class Url {
	//Local server URL
//	public static final String URL_LOGIN = "http://10.0.2.2/track/mobile/login/login.php";// google 模拟器访问本机地址
//	public static final String HOST = "192.168.56.1"; //Genymotion 模拟器访问本机地址
	public static final String WEB_HOST = "pttstage-2.concentrix.com";//WEB SERVER HOST //119.81.125.163
	public static final String XMPP_HOST = "119.81.125.167";//XMPP SERVER HOST
	public static final String PROTOCOL = "http://";
	public static final String DOCUMENT_ROOT = Url.PROTOCOL+Url.WEB_HOST+"/track/mobile";
	public static final String LOGIN_URL = Url.DOCUMENT_ROOT+"/login/login.php";
	public static final String TASKMORNITOR_URL = Url.DOCUMENT_ROOT+"/monitor/index.php";
	public static final String TASKMORNITOR_FOLLOWED_TASK = Url.DOCUMENT_ROOT+"/monitor/follow.php";
	public static final String TASKMORNITOR_SEARCH_URL = Url.DOCUMENT_ROOT+"/monitor/search.php";
	public static final String MESSAGE_CHAT = Url.DOCUMENT_ROOT+"/chat/chatlist.php";
	
	public static final int XMPP_PORT = 5222;
	
}
