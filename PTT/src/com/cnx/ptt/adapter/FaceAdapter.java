package com.cnx.ptt.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cnx.ptt.R;
import com.cnx.ptt.chat.app.XXApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class FaceAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private int currentPage = 0;
	private Map<String, Integer> mFaceMap;
	private List<Integer> faceList = new ArrayList<Integer>();

	public FaceAdapter(Context context, int currentPage) {
		
		this.inflater = LayoutInflater.from(context);
		this.currentPage = currentPage;
		mFaceMap = XXApp.getInstance().getFaceMap();//得到所有表情map集合, 结构："[angry]":12345, "[cry]":12345
		
		initData();
	}

	private void initData() {
		//将 value 添加到faceList 中
		for(Map.Entry<String, Integer> entry:mFaceMap.entrySet()){
			faceList.add(entry.getValue());
		}
	}

	@Override
	public int getCount() {
		return XXApp.NUM + 1;
	}

	@Override
	public Object getItem(int position) {
		return faceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.c_face, null, false);
			viewHolder.faceIV = (ImageView) convertView.findViewById(R.id.face_iv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position == XXApp.NUM) {
			//如果当前位置是最后一个，那么把这个图标设置为删除按钮
			viewHolder.faceIV.setImageResource(R.drawable.emotion_del_selector);
			viewHolder.faceIV.setBackgroundDrawable(null);
		} else {
			int count = XXApp.NUM * currentPage + position;
			if (count < 18) {
				viewHolder.faceIV.setImageResource(faceList.get(count));
			} else {
				viewHolder.faceIV.setImageDrawable(null);
			}
		}
		return convertView;
	}

	public static class ViewHolder {
		ImageView faceIV;
	}
}
