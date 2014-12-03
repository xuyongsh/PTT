package com.cnx.dao;

import java.util.ArrayList;
import java.util.List;

import com.cnx.db.DBHelper;
import com.cnx.db.DBInfo;
import com.cnx.pojo.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDao {
	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	private ContentValues values = null;
	/**
	 * User table column array
	 */
	String[] columns = { DBInfo.Table._ID, DBInfo.Table.USER_ID,
			DBInfo.Table.USER_NAME, DBInfo.Table.USER_EMAIL,
			DBInfo.Table.USER_SESSION, DBInfo.Table.USER_DESC };

	public UserDao(Context context) {
		dbHelper = new DBHelper(context);
	}

	/**
	 * add new user
	 * 
	 * @param user
	 * @return
	 */
	public long inserUser(User user) {
		// get SQLiteDatebase object
		db = dbHelper.getWritableDatabase();
		// binding the object and the value
		values = new ContentValues();

		values.put(DBInfo.Table.USER_ID, user.getUser_id());
		values.put(DBInfo.Table.USER_NAME, user.getUser_name());
		values.put(DBInfo.Table.USER_EMAIL, user.getUser_email());
		values.put(DBInfo.Table.USER_SESSION, user.getUser_session());
		values.put(DBInfo.Table.USER_DESC, user.getUser_desc());

		// execute insert operation
		long rowId = db.insert(DBInfo.Table.USER_TABLE, DBInfo.Table.USER_NAME,
				values);
		// close db
		db.close();

		return rowId;
	}

	/**
	 * update user
	 * 
	 * @param user
	 * @return
	 */
	public int updateUser(User user) {
		return 1;
	}

	/**
	 * delete user by user id
	 * 
	 * @param user_id
	 * @return
	 */
	public int deleteUser(String user_id) {
		return 1;
	}

	/**
	 * get user info by user id
	 * 
	 * @param user_id
	 * @return
	 */
	public User findUserByUserId(String user_id) {
		return null;
	}

	/**
	 * get all user info
	 * 
	 * @return
	 */
	public List<User> findAllUsers() {
		db = dbHelper.getReadableDatabase();
		List<User> userList = null;
		User user = null;
		Cursor cursor = db.query(DBInfo.Table.USER_TABLE, columns, null, null,
				null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			userList = new ArrayList<User>(cursor.getCount());
			while (cursor.moveToNext()) {
				user = new User();

				user.setId(cursor.getLong(cursor
						.getColumnIndex(DBInfo.Table._ID)));
				user.setUser_id(cursor.getString(cursor
						.getColumnIndex(DBInfo.Table.USER_ID)));
				user.setUser_name(cursor.getString(cursor
						.getColumnIndex(DBInfo.Table.USER_NAME)));
				user.setUser_session(cursor.getString(cursor
						.getColumnIndex(DBInfo.Table.USER_SESSION)));
				user.setUser_desc(cursor.getString(cursor
						.getColumnIndex(DBInfo.Table.USER_DESC)));

				userList.add(user);
			}
		}
		cursor.close();
		db.close();

		return userList;
	}
}
