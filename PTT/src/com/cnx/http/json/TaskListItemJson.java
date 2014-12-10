package com.cnx.http.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.cnx.pojo.TaskListItem;
import com.cnx.tag.TAG;

public class TaskListItemJson extends JsonPacket {

	public TaskListItemJson(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static TaskListItemJson taskListItemJson;

	public static TaskListItemJson instance(Context context) {
		if (taskListItemJson == null) {
			taskListItemJson = new TaskListItemJson(context);
		}
		return taskListItemJson;
	}

	public ArrayList<TaskListItem> readJsonTasklistItem(String res) {

		ArrayList<TaskListItem> list = null;

		try {
			if (res == null || res.equals("")) {
				return list;
			}
			JSONObject jsonObject = new JSONObject(res);
			list = new ArrayList<TaskListItem>();

			for (int i = 0; i < jsonObject.length(); i++) {

				JSONObject oj = jsonObject.getJSONObject("" + i);
				list.add(new TaskListItem(oj.getInt("taskid"), oj
						.getString("reqnum"), oj.getString("title"), oj
						.getString("subdate"), oj.getInt("priority"), oj
						.getInt("isfollowed")));
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
