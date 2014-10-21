package com.cnx.ptt;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MessageTab extends ContentTab {

	private ListView messageList;
	private ArrayList<HashMap<String, Object>> messageData;
	private SimpleAdapter listItemAdapter;

	public MessageTab() {
		super();
	}

	public void init(LayoutInflater inflater, ViewGroup container,
			int layout_xml) {
		super.init(inflater, container, layout_xml);

		this.setMessageList((ListView) super.getRoot_view().findViewById(
				R.id.message_list));
		this.setMessageData(new ArrayList<HashMap<String, Object>>());
		this.PrepareData();
		this.GenerateAdapter();
		// Set adapter
		this.getMessageList().setAdapter(this.getListItemAdapter());
	}

	private void PrepareData() {

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.ic_android);
		map.put("ItemTitle", "WPC PTT Team");
		map.put("ItemText", "Welcome to mobile!");
		map.put("ItemTime", "17:30 PM");

		this.getMessageData().add(map);

		map = new HashMap<String, Object>();
		
		map.put("ItemImage", R.drawable.ic_android);
		map.put("ItemTitle", "Theo Wang");
		map.put("ItemText", "Good luck!");
		map.put("ItemTime", "21:30 PM");
		
		this.getMessageData().add(map);
		
		map = new HashMap<String, Object>();
		
		map.put("ItemImage", R.drawable.ic_android);
		map.put("ItemTitle", "Lois Teo");
		map.put("ItemText", "Ok, thanks!");
		map.put("ItemTime", "15:25 PM");
		
		this.getMessageData().add(map);
		
		map = new HashMap<String, Object>();
		
		map.put("ItemImage", R.drawable.ic_android);
		map.put("ItemTitle", "Madhulika Mohan");
		map.put("ItemText", "When can i get the scorecard of this month?");
		map.put("ItemTime", "18:25 PM");
		
		this.getMessageData().add(map);
		
		map = new HashMap<String, Object>();
		
		map.put("ItemImage", R.drawable.ic_android);
		map.put("ItemTitle", "Teerathut Lerstsakwimand");
		map.put("ItemText", "Hi, there?");
		map.put("ItemTime", "18:10 PM");
		
		this.getMessageData().add(map);
		
		map = new HashMap<String, Object>();
		
		map.put("ItemImage", R.drawable.ic_android);
		map.put("ItemTitle", "Robin Li");
		map.put("ItemText", "Let's review it tomorrow... see you.");
		map.put("ItemTime", "06:12 AM");
		
		this.getMessageData().add(map);
		
		map = new HashMap<String, Object>();
		
		map.put("ItemImage", R.drawable.ic_android);
		map.put("ItemTitle", "Lawrence Wong");
		map.put("ItemText", "Please postpone the meeting tomorrow morning.");
		map.put("ItemTime", "08:20 AM");
		
		this.getMessageData().add(map);
		
		map = new HashMap<String, Object>();
		
		map.put("ItemImage", R.drawable.ic_android);
		map.put("ItemTitle", "Cindy Zhao");
		map.put("ItemText", "The bi-weekly meeting has been rescheduled. fyi.");
		map.put("ItemTime", "12:30 PM");
		
		this.getMessageData().add(map);
		
		map = new HashMap<String, Object>();
		
		map.put("ItemImage", R.drawable.ic_android);
		map.put("ItemTitle", "Laura Liu");
		map.put("ItemText", "Ryan is looking for you, where are you?");
		map.put("ItemTime", "10:30 AM");
		
		this.getMessageData().add(map);
		
		map = new HashMap<String, Object>();
		
		map.put("ItemImage", R.drawable.ic_android);
		map.put("ItemTitle", "Gilbert Han");
		map.put("ItemText", "The tasks of this week has been sent to you via email, please review.");
		map.put("ItemTime", "9:30 AM");
		
		this.getMessageData().add(map);
	}

	private void GenerateAdapter() {
		this.setListItemAdapter(new SimpleAdapter(messageList.getContext(),
				this.getMessageData(), R.layout.message_items, new String[] {
						"ItemImage", "ItemTitle", "ItemText", "ItemTime" },
				new int[] { R.id.display_avatar_image, R.id.display_user_name,
						R.id.display_user_message, R.id.display_message_time }));
	}

	public ListView getMessageList() {
		return messageList;
	}

	public void setMessageList(ListView messageList) {
		this.messageList = messageList;
	}

	public ArrayList<HashMap<String, Object>> getMessageData() {
		return messageData;
	}

	public void setMessageData(ArrayList<HashMap<String, Object>> messageData) {
		this.messageData = messageData;
	}

	public SimpleAdapter getListItemAdapter() {
		return listItemAdapter;
	}

	public void setListItemAdapter(SimpleAdapter listItemAdapter) {
		this.listItemAdapter = listItemAdapter;
	}

}
