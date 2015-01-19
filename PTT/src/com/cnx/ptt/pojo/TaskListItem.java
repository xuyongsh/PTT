package com.cnx.ptt.pojo;

public class TaskListItem {

	private Integer t_id;
	private String t_reqnum;
	private String t_title;
	private String t_subdate;
	private Integer t_priority;
	private Integer t_isFollowed = 0;

	public TaskListItem(Integer taskid, String reqnum, String title,
			String subdate, Integer priority, Integer followed) {
		this.setT_id(taskid);
		this.setT_reqnum(reqnum);
		this.setT_title(title);
		this.setT_subdate(subdate);
		this.setT_priority(priority);
		this.setT_isFollowed(followed);
	}

	public Integer getT_id() {
		return t_id;
	}

	public void setT_id(Integer t_id) {
		this.t_id = t_id;
	}

	public Integer getT_isFollowed() {
		return t_isFollowed;
	}

	public void setT_isFollowed(Integer t_isFollowed) {
		this.t_isFollowed = t_isFollowed;
	}

	public Integer getT_priority() {
		return t_priority;
	}

	public void setT_priority(Integer t_priority) {
		this.t_priority = t_priority;
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
