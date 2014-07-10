package com.cnx.ptt;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MessageTab extends ContentTab{

	private ListView messageList;
	private ArrayList<HashMap<String, Object>> messageData;
	private SimpleAdapter listItemAdapter;
	
	public MessageTab(){
		super();
	}
	
	public void init(LayoutInflater inflater, ViewGroup container,
			int layout_xml) {
		super.init(inflater, container, layout_xml);
		
		this.setMessageList((ListView) super.getRoot_view().findViewById(R.id.message_list));
		this.setMessageData( new ArrayList<HashMap<String, Object>>());
		this.PrepareData();
		this.GenerateAdapter();	
		//Set adapter
		this.getMessageList().setAdapter(this.getListItemAdapter());
	}
	
	private void PrepareData()
	{
		//Prepare data and add to message data
		for(int i=0; i < 10; i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_android);
			map.put("ItemTitle", messageList.getContext().getString(R.string.default_user_name));
			map.put("ItemText", messageList.getContext().getString(R.string.default_message));
			map.put("ItemTime", "17:30 PM");
			this.getMessageData().add(map); 
		}
	}
	
	private void GenerateAdapter()
	{
		this.setListItemAdapter(new SimpleAdapter(
				messageList.getContext(), 
				this.getMessageData(),
				R.layout.message_items,
				new String[] {"ItemImage","ItemTitle", "ItemText","ItemTime"}, 
				new int[] {R.id.display_avatar_image,R.id.display_user_name,R.id.display_user_message,R.id.display_message_time} 
		));
	}

	public ListView getMessageList() {
		return messageList;
	}

	public void setMessageList(ListView messageList) {
		this.messageList = messageList;
	}

	public ArrayList <HashMap<String, Object>> getMessageData() {
		return messageData;
	}

	public void setMessageData(ArrayList <HashMap<String, Object>> messageData) {
		this.messageData = messageData;
	}

	public SimpleAdapter getListItemAdapter() {
		return listItemAdapter;
	}

	public void setListItemAdapter(SimpleAdapter listItemAdapter) {
		this.listItemAdapter = listItemAdapter;
	}
	
}
