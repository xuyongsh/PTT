package com.cnx.ptt.test;

import com.cnx.ptt.chat.app.XXApp;
import com.cnx.ptt.db.DBHelper;
import com.cnx.ptt.utils.L;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import android.view.WindowManager;

public class TestCase extends AndroidTestCase {
	private String TAG = "TestCase";
	public void test() {
		DBHelper dbHelper = new DBHelper(getContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("alter table user add column password");
	}
	public void testXXApp(){
//		XXApp.getInstance().initFaceMap();
//		Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), 
//				(Integer) XXApp.getInstance().getFaceMap().values().toArray()[0]);
//		System.out.println("testxxapp"+XXApp.getInstance().getFaceMap().keySet().toArray());
		
		
		
	}
}
