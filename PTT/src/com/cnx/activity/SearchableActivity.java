package com.cnx.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.cnx.utils.LogUtils;
/**
 * This is public search function
 * @author David xu
 *
 */
public class SearchableActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String action = null;
		String query = null;
		// get the bundle data
		Bundle bundle = getIntent().getBundleExtra(SearchManager.APP_DATA);
		action = bundle.getString("action");

		LogUtils.i("SearchableActivity:action", action);
		// here is get search string
		if (getIntent().ACTION_SEARCH.equals(getIntent().getAction())) {
			query = getIntent().getStringExtra(SearchManager.QUERY);
			
			LogUtils.i("SearchableActivity:query", query);
		}
		if ((action == null || action.length() <= 0)) {
			onSearchRequested();
		} else {
			switch (action) {
			case "follow":
				taskSearch(query);
				break;

			default:
				break;
			}
		}
	}
	/**
	 * User can search task by the Search function
	 * @param query The String of the query
	 */
	private void taskSearch(String query){
		Bundle bundle = new Bundle();
		bundle.putString("query", query);
		Intent intent = new Intent(this, TaskSearchActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}
}
