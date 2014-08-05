package com.cnx.ptt;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class TaskRoleSpinnerAdapter extends BaseAdapter implements SpinnerAdapter{

	private ArrayList <TaskRole> m_tr_list;
	private Context mContext;
	
	public TaskRoleSpinnerAdapter(ArrayList <TaskRole> lst, Context c){
		mContext = c;
		m_tr_list = lst;
	}
	
	@Override
	public int getCount() {
		return m_tr_list.size();
	}

	@Override
	public Object getItem(int position) {
		return m_tr_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater _LayoutInflater=LayoutInflater.from(mContext);
		convertView=_LayoutInflater.inflate(R.layout.spinner_item, null);
		TextView _TextView1=(TextView)convertView.findViewById(R.id.sp_item_name);
		TaskRole al = (TaskRole) getItem(position);
		String name = al.getName();
		_TextView1.setText(name);
		
		return convertView;
	}

}
