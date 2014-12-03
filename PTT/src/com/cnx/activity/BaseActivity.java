package com.cnx.activity;


import com.cnx.http.HttpUtil;


import android.app.Activity;
import android.app.Dialog;

public class BaseActivity extends Activity {
	private Dialog progressDialog;
	 /**
     * check the network is available
     * 
     * @return
     */
    public boolean hasNetWork() {
        return HttpUtil.isNetworkAvailable(this);
    }
    /**
     * 
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
    
}
