package com.cnx.ptt.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cnx.ptt.R;
import com.cnx.ptt.adapter.RosterAdapter;
import com.cnx.ptt.pojo.ChatListItemInfo;
import com.cnx.ptt.xmpp.XmppConnectionManager;

public class CopyOfMessageTabActivity extends BaseActivity{
	/*private String TAG = "MessageTabActivity";
	ListView lv_message_list;
	private List<ChatListItemInfo> chatInfos = new ArrayList<ChatListItemInfo>();
	private Roster roster = null;
	private RosterAdapter rosterAdapter;
	private ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_tab);
		lv_message_list = (ListView) findViewById(R.id.lv_message_list);
		pd = new ProgressDialog(this);
		pd.show();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateRoster();
				pd.dismiss();
			}
		}).start();
		
		rosterAdapter = new RosterAdapter(CopyOfMessageTabActivity.this, chatInfos);
		lv_message_list.setAdapter(rosterAdapter);
		lv_message_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getApplicationContext(),
						DisplayMessageActivity.class);
				intent.putExtra("c_item_name", chatInfos.get(position)
						.getName());
				intent.putExtra("c_item_id", chatInfos.get(position)
						.getUser());
				startActivity(intent);
				return;
			}

		});
	}
	*//**
	 * update chat list
	 *//*
	public void updateRoster() {
		// get all of the chat list
		
		roster = XmppConnectionManager.getConnection().getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		Log.i(TAG, "chatlist size:" + entries.size());
		for (RosterEntry entry : entries) {
			Log.i(TAG, "entry.getUser():" + entry.getUser());
			//
			Presence presence = roster.getPresence(entry.getUser());
			ChatListItemInfo chat = new ChatListItemInfo();
			chat.setName(entry.getName());
			chat.setUser(entry.getUser());
			chat.setType(entry.getType());
			chat.setSize(entry.getGroups().size());
			// user status
			chat.setStatus(presence.getStatus());
			Log.i(TAG, "user status:" + chat.getStatus());
			chat.setFrom(presence.getFrom());

			chatInfos.add(chat);
		}
		Log.i(TAG, "chatlist length:" + chatInfos.size());
	}*/
}
