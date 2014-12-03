package com.cnx.ptt;

import java.util.ArrayList;

import com.cnx.ptt.R;
import com.cnx.ptt.R.id;
import com.cnx.ptt.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class TaskActionSpinnerAdapter extends BaseAdapter implements SpinnerAdapter   {
	
	private ArrayList <TaskAction> m_ta_list;
	private Context mContext;
	
	public TaskActionSpinnerAdapter(ArrayList <TaskAction> lst, Context  c){
		mContext = c;
		m_ta_list = lst;
	}

	@Override
	public int getCount() {
		return m_ta_list.size();
	}

	@Override
	public Object getItem(int position) {
		return m_ta_list.get(position);
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
		TaskAction al = (TaskAction) getItem(position);
		String name = al.getName();
		_TextView1.setText(name);
		return convertView;
	}

}
