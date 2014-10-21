package com.cnx.ptt;

public class TaskListItem {
	
	private String m_name;
	private String m_news;
	private String m_time;
	
	public String getM_name() {
		return m_name;
	}
	public void setM_name(String m_name) {
		this.m_name = m_name;
	}
	public String getM_news() {
		return m_news;
	}
	public void setM_news(String m_news) {
		this.m_news = m_news;
	}
	
	public TaskListItem(String na, String ne, String tm){
		this.setM_name(na);
		this.setM_news(ne);
		this.setM_time(tm);
	}
	public String getM_time() {
		return m_time;
	}
	public void setM_time(String m_time) {
		this.m_time = m_time;
	}

}
