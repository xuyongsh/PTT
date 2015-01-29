package com.cnx.ptt.test;

import com.cnx.ptt.db.DBHelper;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class TestCase extends AndroidTestCase {
	private String TAG = "TestCase";
	public void test() {
		DBHelper dbHelper = new DBHelper(getContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("alter table user add column password");
	}
}
