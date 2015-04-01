package com.cnx.ptt.chat.app;

import java.util.LinkedHashMap;
import java.util.Map;

import com.cnx.ptt.R;
import com.cnx.ptt.chat.utils.PreferenceConstants;
import com.cnx.ptt.chat.utils.PreferenceUtils;
import com.cnx.ptt.utils.CrashHandler;
import com.cnx.ptt.utils.L;

import android.app.Application;

public class XXApp extends Application {
	public static final int NUM_PAGE = 1;// 总共有多少页
	public static int NUM = 20;// 每页20个表情,还有最后一个删除button, 一共21个格
	private Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();
	private static XXApp mApplication;

	public synchronized static XXApp getInstance() {
		return mApplication;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
//		L.DEBUG = PreferenceUtils.getPrefBoolean(this,
//				PreferenceConstants.ISNEEDLOG, true);
		if (PreferenceUtils.getPrefBoolean(this,
				PreferenceConstants.REPORT_CRASH, true))
			CrashHandler.getInstance().init(this);
		initFaceMap();
	}

	public Map<String, Integer> getFaceMap() {
		if (!mFaceMap.isEmpty())
			return mFaceMap;
		L.i("mFaceMap====="+mFaceMap);
		return null;
	}

	public void initFaceMap() {
		mFaceMap.put("[angry]", R.drawable.f_static_000);
		mFaceMap.put("[blush]", R.drawable.f_static_001);
		mFaceMap.put("[?:|]", R.drawable.f_static_002);
		mFaceMap.put("[cool]", R.drawable.f_static_003);
		mFaceMap.put("[cry]", R.drawable.f_static_004);
		mFaceMap.put("[devil]", R.drawable.f_static_005);
		mFaceMap.put("[:'(]", R.drawable.f_static_006);
		mFaceMap.put("[grin]", R.drawable.f_static_007);
		mFaceMap.put("[happy]", R.drawable.f_static_008);
		mFaceMap.put("[laugh]", R.drawable.f_static_009);
		mFaceMap.put("[love]", R.drawable.f_static_010);
		mFaceMap.put("[:-(]", R.drawable.f_static_011);
		mFaceMap.put("[party]", R.drawable.f_static_012);
		mFaceMap.put("[plain]", R.drawable.f_static_013);
		mFaceMap.put("[sad]", R.drawable.f_static_014);
		mFaceMap.put("[:-P]", R.drawable.f_static_015);
		mFaceMap.put("[silly]", R.drawable.f_static_016);
		mFaceMap.put("[sleepy]", R.drawable.f_static_017);
		mFaceMap.put("[wink]", R.drawable.f_static_018);
		
	}
}
