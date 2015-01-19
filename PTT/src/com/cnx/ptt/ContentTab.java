package com.cnx.ptt;

import com.cnx.ptt.activity.BaseActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ContentTab extends BaseActivity{
	
	private int layout_resource;
	private View root_view;
	
	public void init( LayoutInflater inflater, ViewGroup container, int layout_xml)
	{
		this.setLayout_resource(layout_xml);
		this.setRoot_view(inflater.inflate(this.getLayout_resource(), container,
					false));
	}

	public int getLayout_resource() {
		return layout_resource;
	}

	public void setLayout_resource(int layout_resource) {
		this.layout_resource = layout_resource;
	}

	public View getRoot_view() {
		return root_view;
	}

	public void setRoot_view(View root_view) {
		this.root_view = root_view;
	}
}
