package com.cnx.ptt;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactListAdapter extends BaseAdapter {

	private Context context;  
    private List<Person> list;
    private ViewHolder viewHolder;
    
    public ContactListAdapter(Context context, List<Person> list){
    	
    	this.setContext(context);
    	this.setList(list);
    	
    }
	
	@Override
	public int getCount() {
		return this.getList().size();
	}

	@Override
	public Object getItem(int position) {
		
		return this.getList().get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	private class ViewHolder {  
        private TextView indexTv;  
        private TextView itemTv;
		public TextView getIndexTv() {
			return indexTv;
		}
		public  void setIndexTv(TextView indexTv) {
			this.indexTv = indexTv;
		}
		public TextView getItemTv() {
			return itemTv;
		}
		public void setItemTv(TextView itemTv) {
			this.itemTv = itemTv;
		}  
    } 
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		String item_name = this.getList().get(position).getName();
		viewHolder = new ViewHolder(); 
		
		/*if(this.isEnabled(position))
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_item, null);
			viewHolder.setItemTv((TextView) convertView.findViewById(R.id.contact_person_name));
			viewHolder.getItemTv().setText(item_name);
		}
		else
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_contact_tab, null);
			viewHolder.setIndexTv((TextView) convertView.findViewById(R.id.index_name));
			viewHolder.getIndexTv().setText(item_name);
		}
		*/
		return convertView;
	}
	
	 
	
	@Override
	public boolean isEnabled(int position){
		return this.getList().get(position).getIs_active();
		
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public List<Person> getList() {
		return list;
	}

	public void setList(List<Person> list) {
		this.list = list;
	}

}
