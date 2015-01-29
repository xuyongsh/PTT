package com.cnx.ptt.pojo;

public class ChatMsg {
	String userid;
	String msg;
	String date;
	String from;
	
	public ChatMsg(String userid, String msg, String date, String from) {
		super();
		this.userid = userid;
		this.msg = msg;
		this.date = date;
		this.from = from;
	}
	@Override
	public String toString() {
		return "Message [userid=" + userid + ", msg=" + msg + ", date=" + date
				+ ", from=" + from + "]";
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	
}
