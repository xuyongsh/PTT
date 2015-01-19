package com.cnx.ptt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cnx.ptt.adapter.RosterAdapter;
import com.cnx.ptt.pojo.ChatListItemInfo;
import com.cnx.ptt.xmpp.XmppConnectionManager;

public class MessageTab extends ContentTab {

	private String TAG = "MessageTab";
	// message list view
	public ListView lv_messageList;

	public List<ChatListItemInfo> chatInfos = new ArrayList<ChatListItemInfo>();

	private Roster roster = null;
	private RosterAdapter rosterAdapter;

	public void init(LayoutInflater inflater, ViewGroup container,
			int layout_xml) {
		super.init(inflater, container, layout_xml);

		lv_messageList = (ListView) super.getRoot_view().findViewById(
				R.id.message_list);
		chatInfos.clear();

		updateRoster();
		rosterAdapter = new RosterAdapter(inflater, chatInfos);
		lv_messageList.setAdapter(rosterAdapter);

	}

	/**
	 * init chatlist
	 */
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
	}

}
