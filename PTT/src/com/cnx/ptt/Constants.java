package com.cnx.ptt;



public class Constants {
	//TODO: ����demo�� ��Ҫ�޸�
	public static final String WS_URI = "ws://192.168.1.100:9000";
	public static final String WAMP_EVENT_HTTP_URI = "http://live.pttgps.com";
	public static final String WAMP_EVENT_PREFIX = "event";
	public static final int DEBUG_CLIENT_ID = 1;
	public static final int DEBUG_TARGET_ID = 2;
	public static final String TEST_EVENT = "http://example.com/myEvent1";
	public static final String CURRENT_USER_INFO = "currentUserInfo";
	public static final String USER_ID = "s_userid";
	public static final String USER_NAME="s_username";
	public static final String SESSIONID = "s_sessionid";
	//��ѯ����� 10����
	public static final Long POLLING_PERIOD = (long) 3000;// 1000*60*10 = 600000  ;   ���ԣ�3000
	
	//������ѯ�㲥
	public static final String POLLING_FOLLOW_TASK = "com_cnx_ptt_polling_follow_task";
	
}
