package com.cnx.ptt;

public final class TaskAction {
	
	private String name;
	private int id;
	
	public TaskAction(String n, int i){
		
		this.setName(n);
		this.setId(i);
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
