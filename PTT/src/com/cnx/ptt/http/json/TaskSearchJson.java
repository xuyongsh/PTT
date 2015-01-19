package com.cnx.ptt.http.json;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;

import com.cnx.ptt.pojo.TaskListItem;
import com.cnx.ptt.tag.TAG;

public class TaskSearchJson extends JsonPacket {

	public TaskSearchJson(Context context) {
		super(context);
	}

	public static TaskSearchJson taskSearchJson;

	public static TaskSearchJson instance(Context context) {
		if (taskSearchJson == null) {
			taskSearchJson = new TaskSearchJson(context);
		}
		return taskSearchJson;
	}

	public ArrayList<TaskListItem> readJsonTaskSearch(String res) {

		ArrayList<TaskListItem> list = null;

		try {
			if (res == null || res.equals("")) {
				return list;
			}
			list = new ArrayList<TaskListItem>();
			JSONObject jsonObject = new JSONObject(res);
            if (jsonObject.has(TAG.RESPONSE_FLAG) && jsonObject.getString(TAG.RESPONSE_FLAG).equals(TAG.RESPONSE_SUC)) {
            	list.add(new TaskListItem(jsonObject.getInt("taskid"), 
            			jsonObject.getString("reqnum"), jsonObject.getString("title"), 
            			jsonObject.getString("subdate"), jsonObject.getInt("priority"), 
            			0));
            	return list;
            }
			
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.gc();
		}
		return list;
	}
}
