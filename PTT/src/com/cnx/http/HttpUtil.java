
package com.cnx.http;

import org.apache.http.NameValuePair;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class HttpUtil {

 
    public static boolean isNetworkAvailable(Context context) {
        return NetWorkHelper.isNetworkAvailable(context);
    }
    public static String postByHttpClient(Context context, String strUrl,
            NameValuePair... nameValuePairs) throws Exception {
        String result = CustomHttpClient.PostFromWebByHttpClient(context, strUrl, nameValuePairs);
        return result;
    }
    public static String getByHttpClient(Context context, String strUrl,
            NameValuePair... nameValuePairs) throws Exception {
        String result = CustomHttpClient.getFromWebByHttpClient(context, strUrl, nameValuePairs);

        if (TextUtils.isEmpty(result)) {
            result = "";
        }

        return result;
    }
    
    /**
     * check WIFI is available
     */
    public static boolean isWifiDataEnable(Context context) {
        String TAG = "httpUtils.isWifiDataEnable()";
        try {
            return NetWorkHelper.isWifiDataEnable(context);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
