package com.cnx.pojo;

public class TaskListItem {

	private String t_reqnum;
	private String t_title;
	private String t_subdate;

	public TaskListItem(String na, String ne, String tm) {
		this.setT_reqnum(na);
		this.setT_title(ne);
		this.setT_subdate(tm);
	}

	public String getT_reqnum() {
		return t_reqnum;
	}

	public void setT_reqnum(String t_reqnum) {
		this.t_reqnum = t_reqnum;
	}

	public String getT_title() {
		return t_title;
	}

	public void setT_title(String t_title) {
		this.t_title = t_title;
	}

	public String getT_subdate() {
		return t_subdate;
	}

	public void setT_subdate(String t_subdate) {
		this.t_subdate = t_subdate;
	}

}
