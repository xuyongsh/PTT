package com.cnx.ptt.dao;

import java.util.ArrayList;
import java.util.List;

import com.cnx.ptt.db.DBHelper;
import com.cnx.ptt.db.DBInfo;
import com.cnx.ptt.pojo.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDao {
	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	private ContentValues values = null;

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
		values.put(DBInfo.Table.USER_PASSWORD, user.getUser_pwd());

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
	public Boolean updateUser(User user, int _id) {
		db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			ContentValues values = new ContentValues();
			values.put(DBInfo.Table.USER_ID, user.getUser_id());
			values.put(DBInfo.Table.USER_EMAIL, user.getUser_name());
			values.put(DBInfo.Table.USER_PASSWORD, user.getUser_pwd());
			// return value is integer
			db.update(DBInfo.Table.USER_TABLE, values, "_id = ?", new String[]{String.valueOf(_id)});
			db.close();
			return true;
		}
		
		return false;
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
	 * 
	 * @param user_email
	 * @return Boolean
	 */
	public int finUserByUserEmail(String user_email) {
		db = dbHelper.getReadableDatabase();
		int _id = -1;
		Cursor cursor = db.query(DBInfo.Table.USER_TABLE, new String[]{"_id"}, "user_name = ?", new String[]{user_email}, null, null, null);
		if(db.isOpen())
		{
			if(cursor != null && cursor.moveToFirst()){
				_id = cursor.getInt(0);
			}
			cursor.close();
			db.close();
			return _id;
		}
		return _id;
	}

	/**
	 * get all user info
	 * 
	 * @return
	 */
	public List<User> findAllUsers() {
		db = dbHelper.getReadableDatabase();
		List<User> userList = null;
		/**
		 * User table column array
		 */
		String[] columns = { DBInfo.Table.USER_ID, DBInfo.Table.USER_EMAIL,DBInfo.Table.USER_PASSWORD};
		Cursor cursor = db.query(DBInfo.Table.USER_TABLE, columns, null, null,
				null, null, null);

		int user_id;
		String user_email;
		String user_pwd;
		if (cursor != null && cursor.getCount() > 0) {
			userList = new ArrayList<User>(cursor.getCount());
			while (cursor.moveToNext()) {
				user_id = cursor.getInt(0);
				user_email = cursor.getString(1);
				user_pwd = cursor.getString(2);
				userList.add(new User(user_email, user_pwd));
			}
		}
		cursor.close();
		db.close();

		return userList;
	}
}
