
package com.cnx.ptt.http;

import android.util.Log;

import org.apache.http.NameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CustomHttpURLConnection {
    private static String TAG = "CustomHttpUrlConnection";
    private static HttpURLConnection conn;

    public CustomHttpURLConnection() {
    }

    public static String GetFromWebByHttpUrlConnection(String strUrl,
            NameValuePair... nameValuePairs) {
        String result = "";
        try {
            URL url = new URL(strUrl);

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(4000);
            conn.setRequestProperty("accept", "*/*");
            // int resCode=conn.getResponseCode();
            conn.connect();
            InputStream stream = conn.getInputStream();
            InputStreamReader inReader = new InputStreamReader(stream);
            BufferedReader buffer = new BufferedReader(inReader);
            String strLine = null;
            while ((strLine = buffer.readLine()) != null)
            {
                result += strLine;
            }
            inReader.close();
            conn.disconnect();
            return result;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "getFromWebByHttpUrlCOnnection:" + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "getFromWebByHttpUrlCOnnection:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String PostFromWebByHttpURLConnection(String strUrl,
            NameValuePair... nameValuePairs) {
        String result = "";
        try {
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url
                    .openConnection();
            conn.setDoInput(true);
           
            conn.setDoOutput(true);
           
            conn.setRequestMethod("POST");
           
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(4000);
            
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            

            InputStream in = conn.getInputStream();
            InputStreamReader inStream = new InputStreamReader(in);
            BufferedReader buffer = new BufferedReader(inStream);
            String strLine = null;
            while ((strLine = buffer.readLine()) != null)
            {
                result += strLine;
            }
            return result;
        } catch (IOException ex) {
            Log.e(TAG, "PostFromWebByHttpURLConnection+ ex.getMessage()");
            ex.printStackTrace();
            return null;
        }
    }

}
