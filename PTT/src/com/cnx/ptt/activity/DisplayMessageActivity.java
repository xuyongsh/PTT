package com.cnx.ptt.activity;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cnx.ptt.R;
import com.cnx.ptt.pojo.ChatMsg;
import com.cnx.ptt.utils.TimeRender;
import com.cnx.ptt.xmpp.XmppConnectionManager;

public class DisplayMessageActivity extends BaseActivity {

	private List<ChatMsg> chatMsgList = new ArrayList<ChatMsg>();
	private MyAdapter adapter;
	private EditText sendMessageTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_chat_dialog);
		Bundle extras = getIntent().getExtras();
		final String userJID = extras.getString("c_item_id");
		final CharSequence userName = extras.getCharSequence("c_item_name");
		super.setTitle(userName);

		ListView lv_display_message = (ListView) findViewById(R.id.lv_display_message);
		lv_display_message.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		sendMessageTxt = (EditText) findViewById(R.id.input_message);
		adapter = new MyAdapter(this);
		lv_display_message.setAdapter(adapter);
		//message listener
		ChatManager cm = XmppConnectionManager.getConnection().getChatManager();
		
		final Chat newchat = cm.createChat(userJID, null);
		cm.addChatListener(new ChatManagerListener() {

			@Override
			public void chatCreated(Chat chat, boolean able) {
				chat.addMessageListener(new MessageListener() {
					
					@Override
					public void processMessage(Chat chat2,
							org.jivesoftware.smack.packet.Message message) {
						System.out.println(message.getFrom());
						if(message.getFrom().contains(userJID)){
							String[] args = new String[] { (String) userName, message.getBody(), TimeRender.getDate(), "IN" };
							android.os.Message msg = mHandler.obtainMessage();
							msg.what = 1;
							msg.obj = args;
							msg.sendToTarget();
						}else{
							// orther user / group / admin of the openfire
							// do work...
						}
						
					}
				});
			}
		});
		//send message button
		findViewById(R.id.button_send_now).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String msg = sendMessageTxt.getText().toString().trim();
				if(msg.length() > 0){
					
					chatMsgList.add(new ChatMsg(UserSession.user.getUser_name(), msg, TimeRender.getDate(), "OUT"));
					adapter.notifyDataSetChanged();
					try {
						newchat.sendMessage(msg);
					} catch (XMPPException e) {
						e.printStackTrace();
					}
				}
				sendMessageTxt.setText("");
			}
		});
		
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				String[] args = (String[]) msg.obj;
				chatMsgList.add(new ChatMsg(args[0], args[1], args[2], args[3]));
				adapter.notifyDataSetChanged();
				break;			
			
			default:
				break;
			}
		};
	};
	class MyAdapter extends BaseAdapter {

		private Context cxt;
		private LayoutInflater inflater;

		public MyAdapter(DisplayMessageActivity formClient) {
			this.cxt = formClient;
		}

		@Override
		public int getCount() {
			return chatMsgList.size();
		}

		@Override
		public Object getItem(int position) {
			return chatMsgList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			this.inflater = (LayoutInflater) this.cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(chatMsgList.get(position).getFrom().equals("IN")){
				convertView = this.inflater.inflate(R.layout.formclient_chat_in, null);
			}else{
				convertView = this.inflater.inflate(R.layout.formclient_chat_out, null);
			}
			TextView useridView = (TextView) convertView.findViewById(R.id.formclient_row_userid);
			TextView dateView = (TextView) convertView.findViewById(R.id.formclient_row_date);
			TextView msgView = (TextView) convertView.findViewById(R.id.formclient_row_msg);
			
			useridView.setText(chatMsgList.get(position).getUserid());
			dateView.setText(chatMsgList.get(position).getDate());
			msgView.setText(chatMsgList.get(position).getMsg());
			return convertView;
		}
	}
}
