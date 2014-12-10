package com.cnx.activity;


import android.app.Activity;
import android.app.Dialog;

import com.cnx.http.HttpUtil;
import com.cnx.utils.DialogUtil;

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
    
}
