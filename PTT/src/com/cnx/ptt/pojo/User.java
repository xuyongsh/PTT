package com.cnx.ptt.pojo;

public class User {

	private int user_id;
	private String user_email;
	private String user_name;

	private String user_session;
	private String user_desc;
	private String user_pwd;

	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", user_email="
				+ user_email + ", user_name=" + user_name + "user_session"
				+ user_session + ", user_desc=" + user_desc + "]";
	}
	public User() {
		super();
	}

	public User(String user_email,  String user_pwd) {
		super();
		this.user_email = user_email;
		this.user_pwd = user_pwd;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_session() {
		return user_session;
	}

	public void setUser_session(String user_session) {
		this.user_session = user_session;
	}

	public String getUser_desc() {
		return user_desc;
	}

	public void setUser_desc(String user_desc) {
		this.user_desc = user_desc;
	}
	public String getUser_pwd() {
		return user_pwd;
	}

	public void setUser_pwd(String user_pwd) {
		this.user_pwd = user_pwd;
	}
}
