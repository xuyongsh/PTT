package com.cnx.ptt.pojo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.cnx.ptt.Constants;

public final class Role {
	public static final Context Context = null;
	private String name;
	private int id;
	private static ArrayList<Role> roleList;
	

	public Role(String name, int id){
		setRoleList();
//		this.setName(name);
//		this.setId(id);
	}
	public Role(){
		setRoleList();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<Role> getRoleList(){
		return roleList;
	}
	public void setRoleList(){
		Thread roleThread = new Thread(new RoleListHttp());
		roleThread.start();
	}
	public void parseJsonMulti(String strResult) { 
		String s = "";
		
		try { 
			JSONArray jsonObjs = new JSONObject(strResult).getJSONArray("rolelist"); 
			
			for(int i = 0; i < jsonObjs.length() ; i++){ 
				//JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i)).getJSONObject("rolelist"); 
				int id = ((JSONObject)jsonObjs.opt(i)) .getInt("id"); 
				String name = ((JSONObject)jsonObjs.opt(i)) .getString("name"); 
				
				s +=  "roleid:"+id + ", role:" + name + "\n" ;
//				roleList.add(id, name);
				
			} 
			
		} catch (JSONException e) { 
			System.out.println("Jsons parse error !"); 
			e.printStackTrace(); 
		}
		
		//return roleList;
	} 
	
	class RoleListHttp implements Runnable {
		@Override
		public void run() {
			// connection SAE server
	    	HttpPost mPost = new HttpPost("http://pttmobile.sinaapp.com/role/rolelist.php");
			
			/**List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
			SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);	
	    	String username = sp.getString("username", null);
	    	String password = sp.getString("password", null);
			
			pairs.add(new BasicNameValuePair("userid", userid));
			pairs.add(new BasicNameValuePair("session", sessionid));
			
			try {
				mPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				HttpResponse response = new DefaultHttpClient().execute(mPost);
				int res = response.getStatusLine().getStatusCode();
				
				if (res == 200) {
					HttpEntity entity = response.getEntity();
					
					if (entity != null) {
						String info = EntityUtils.toString(entity);
						//the following code is analyze the return data from server 				
						parseJsonMulti(info);
						
						if(true){
							//save the user's data in session					
							DefaultConfig.session.put("s_userid", userid);
							DefaultConfig.session.put("s_sessionid", sessionid);
						}
						
					}
				}
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}**/
		}

	}
	

}
