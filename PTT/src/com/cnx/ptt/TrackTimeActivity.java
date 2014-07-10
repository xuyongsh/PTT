package com.cnx.ptt;

import com.cnx.ptt.zxing.CaptureCodeActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class TrackTimeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_time);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.track_time, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.action_capture_code){
			Intent i = new Intent(this.getBaseContext(), CaptureCodeActivity.class);
			this.startActivityForResult(i,0);
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
		{
			TextView tv = (TextView) this.findViewById(R.id.tv_show_result);
			tv.setText(data.getStringExtra("SCAN_RESULT"));
			Log.i("TT", "RESULT_OK");
		}
	}
}
