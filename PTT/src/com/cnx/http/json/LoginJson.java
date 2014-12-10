package com.cnx.http.json;

import org.json.JSONObject;

import android.content.Context;

import com.cnx.pojo.User;
import com.cnx.tag.TAG;

public class LoginJson extends JsonPacket {

    public static LoginJson loginJson;


    public LoginJson(Context context) {
        super(context);
    }

    public static LoginJson instance(Context context) {
        if (loginJson == null) {
        	loginJson = new LoginJson(context);
        }
        return loginJson;
    }

    public User readJsonLoginModels(String res) {
        User user = null;
        
        try {
            if (res == null || res.equals("")) {
                return user;
            }
            JSONObject jsonObject = new JSONObject(res);
            if (jsonObject.has(TAG.RESPONSE_FLAG) && jsonObject.getString(TAG.RESPONSE_FLAG).equals(TAG.RESPONSE_SUC)) {
            	user = new User();
            	user.setUser_id(jsonObject.getString("userid"));
            	user.setUser_email(jsonObject.getString("email"));
            	user.setUser_name(jsonObject.getString("name"));
            	
            	return user;
            }
        } catch (Exception e) {

        } finally {
            System.gc();
        }
        return user;
    }
}
