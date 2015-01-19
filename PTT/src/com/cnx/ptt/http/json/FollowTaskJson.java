package com.cnx.ptt.http.json;


import org.json.JSONObject;

import android.content.Context;

import com.cnx.ptt.tag.TAG;

public class FollowTaskJson extends JsonPacket {

	public static FollowTaskJson followTaskJson;

	public FollowTaskJson(Context context) {
		super(context);
	}

	public static FollowTaskJson instance(Context context) {
		if (followTaskJson == null) {
			followTaskJson = new FollowTaskJson(context);
		}
		return followTaskJson;
	}

	public Boolean readJsonFollowTask(String res) {

		try {
			if (res == null || res.equals("")) {
				return false;
			}
			JSONObject jsonObject = new JSONObject(res);
			if (jsonObject.has(TAG.RESPONSE_FLAG)
					&& jsonObject.getString(TAG.RESPONSE_FLAG).equals(
							TAG.RESPONSE_SUC)) {
				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
	}
}
