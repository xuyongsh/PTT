package com.cnx.ptt;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ExploreModuleListAdapter extends BaseAdapter{
	
	private Context context;  
    private List<ExploreModule> list;
	
	public ExploreModuleListAdapter(Context context, List<ExploreModule> list){
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(this.getContext()).inflate(
				this.getList().get(position).getTemplate(), null
				);
		
		TextView tv = (TextView) convertView.findViewById(R.id.explore_module_item_tv);
		tv.setText(this.getList().get(position).getTitle());
		
		tv.setCompoundDrawablesWithIntrinsicBounds(this.getList().get(position).getIcon(), 0, 0, 0);
		
		return convertView;
	}

	public List<ExploreModule> getList() {
		return list;
	}

	public void setList(List<ExploreModule> list) {
		this.list = list;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
