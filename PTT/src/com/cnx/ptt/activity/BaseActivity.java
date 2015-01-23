package com.cnx.ptt.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;

import com.cnx.ptt.http.HttpUtil;
import com.cnx.ptt.pojo.User;
import com.cnx.ptt.utils.DialogUtil;

public class BaseActivity extends Activity {
	public SharedPreferences sp;
	
	public Dialog progressDialog;
	 /**
     * check the network is available
     * 
     * @return
     */
    public boolean hasNetWork() {
        return HttpUtil.isNetworkAvailable(this);
    }
    
    /**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show, final View displayView, final View hideView) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			displayView.setVisibility(View.VISIBLE);
			displayView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							displayView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			hideView.setVisibility(View.VISIBLE);
			hideView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							hideView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			displayView.setVisibility(show ? View.VISIBLE : View.GONE);
			hideView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
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
