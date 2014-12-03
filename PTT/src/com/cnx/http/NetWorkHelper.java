
package com.cnx.http;

import android.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

public class NetWorkHelper {

	private static NetWorkHelper instance = null;
	public static NetWorkHelper getInstance() {
        if (instance == null) {
            instance = new NetWorkHelper();
        }
        return instance;
	}
    public static void checkNetwork(final Context context) {
		if (!isNetworkAvailable(context)) {
			TextView msg = new TextView(context);
			msg.setText("Network disabled, please setting£¡");
			AlertDialog.Builder builder = new Builder(context);
			builder.setIcon(R.drawable.ic_dialog_alert)
					.setTitle("Network")
					.setView(msg)
					.setPositiveButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
			builder.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					context.startActivity(new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS));
					dialog.dismiss();
				}
			});
			builder.create().show();
		}
	}
    /**
     * @return true: network is available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * ÅÐ¶Ïwifi ÊÇ·ñ¿ÉÓÃ
     * 
     * @param context
     * @return
     * @throws Exception
     */
    public static boolean isWifiDataEnable(Context context) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiDataEnable = false;
        isWifiDataEnable = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        return isWifiDataEnable;
    }
}
