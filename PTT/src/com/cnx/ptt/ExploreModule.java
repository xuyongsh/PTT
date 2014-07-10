package com.cnx.ptt;

public class ExploreModule {
	
	private int template;
	private int title;
	private int icon;
	private String mesage;
	
	public ExploreModule(int templ, int icon, int title){
		this.setTemplate(templ);
		this.setIcon(icon);
		this.setTitle(title);
	}
	
	public int getTemplate() {
		return template;
	}
	public void setTemplate(int template) {
		this.template = template;
	}
	public int getTitle() {
		return title;
	}
	public void setTitle(int title) {
		this.title = title;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public String getMesage() {
		return mesage;
	}
	public void setMesage(String mesage) {
		this.mesage = mesage;
	}
	
	

}
