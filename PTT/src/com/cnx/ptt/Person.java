package com.cnx.ptt;

public class Person {
	private String name;
	private int image;
	private boolean is_active;
	
	public Person(String name, boolean active) {  
        super();  
        this.setName(name);  
        this.setImage(R.drawable.ic_android);
        this.setIs_active(active);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public boolean getIs_active() {
		return is_active;
	}

	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}
}
