package com.cnx.ptt.adapter;

import java.util.List;
import java.util.zip.Inflater;

import com.cnx.ptt.R;
import com.cnx.ptt.pojo.ChatListItemInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CopyOfRosterAdapter extends BaseAdapter {
	private List<ChatListItemInfo> chatInfos;
	LayoutInflater mInflater;
	public CopyOfRosterAdapter(Context context, List<ChatListItemInfo> chatInfos) {
		mInflater = LayoutInflater.from(context);;
		this.chatInfos = chatInfos;
	}

	@Override
	public int getCount() {
		return chatInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return chatInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		ImageView userAvatar;
		TextView userName;
		TextView userStatus;
		TextView lastTime;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ChatListItemInfo chat = chatInfos.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.message_item, null);

			holder.userAvatar = (ImageView) convertView.findViewById(R.id.display_avatar_image);
			holder.userName = (TextView)convertView.findViewById(R.id.display_user_name);
			holder.userStatus = (TextView)convertView.findViewById(R.id.display_user_message);
			holder.lastTime = (TextView) convertView.findViewById(R.id.display_message_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.userAvatar.setImageResource(R.drawable.iv_avatar_1);
		holder.userName.setText(chat.getName());
		holder.userStatus.setText(chat.getStatus());
		holder.lastTime.setText(chat.getType()+"");
		return convertView;
	}
}
