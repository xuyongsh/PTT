package com.cnx.ptt.activity;


import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cnx.ptt.http.HttpUtil;
import com.cnx.ptt.pojo.User;
import com.cnx.ptt.utils.DialogUtil;

public class BaseActivity extends Activity {
	public SharedPreferences sp;
	
	public Dialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	 /**
     * check the network is available
     * 
     * @return
     */
    public boolean hasNetWork() {
        return HttpUtil.isNetworkAvailable(this);
    }
   
    /**
     * Display progress Dialog
     * 
     * 
     */
    public void showProgressDialog() {
        try {

            if (progressDialog == null) {
                progressDialog = DialogUtil.createLoadingDialog(this);
            }
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * dismiss ProgressDialog
     */
    public void dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * system.out.print function
     * @param o
     * @param args
     */
    public final void sout(Object o, Object... args) {

		if (o != null && args != null && args.length > 0) {

			String s = o.toString();

			for (int i = 0; i < args.length; i++) {

				String item = args[i] == null ? "" : args[i].toString();

				if (s.contains("{" + i + "}")) {

					s = s.replace("{" + i + "}", item);

				} else {

					s += " " + item;
				}
			}

			System.out.println(s);
		}
	}
    public static class UserSession {
		public static User user;
	}
   
}
