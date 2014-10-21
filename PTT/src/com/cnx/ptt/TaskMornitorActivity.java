package com.cnx.ptt;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TaskMornitorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_mornitor);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_mornitor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private ArrayList<TaskListItem> item_list;
		private TaskListAdapter task_list_adp;
		private ListView task_list;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_task_mornitor,
					container, false);

			prepare_data();

			task_list = (ListView) rootView
					.findViewById(R.id.task_mornitor_list);

			task_list_adp = new TaskListAdapter(rootView.getContext(),
					item_list);

			task_list.setAdapter(task_list_adp);

			task_list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent_display_task = new Intent(
							parent.getContext(), DisplayTaskActivity.class);
					startActivity(intent_display_task);

				}

			});

			return rootView;
		}

		private void prepare_data() {
			item_list = new ArrayList<TaskListItem>();
			item_list
					.add(new TaskListItem(
							"TV/Audio/Video > Home Theatre > Home Theatre System > HW-H7501 (Primary)",
							"Step changed from [WPL] WPL review to [WPL] Need appr. (LOM review)",
							"Sep 26, 2014 18:15 HKT [10:15 GMT]"));
			item_list
					.add(new TaskListItem(
							"Home Appliances > Vacuum Cleaner > Bagless Type > SC20F30WB (Primary) ",
							"Step changed from [WB] Submit to [WPS] Need Approval",
							"Sep 29, 2014 17:36 HKT [09:36 GMT]"));
			item_list
					.add(new TaskListItem(
							"PC/Peripherals/Printer > Printer/Multifunction > Consumables > MLT-D103L (Primary) ",
							"Step changed from [WPL] New task to [WPM] Cancel request",
							"Sep 08, 2014 09:18 HKT [01:18 GMT]"));
			item_list
					.add(new TaskListItem(
							"PC/Peripherals/Printer > Printer/Multifunction > Consumables > MLT-D105L (Primary)",
							"Step changed from [WPL] New task to [WPM] Cancel request",
							"Sep 08, 2014 09:19 HKT [01:19 GMT]"));
			item_list
					.add(new TaskListItem(
							"PC/Peripherals/Printer > Printer/Multifunction > Consumables > MLT-D101S (Primary)",
							"Step changed from [WPL] New task to [WPM] Cancel request",
							"Sep 08, 2014 09:19 HKT [01:19 GMT]"));
			item_list
					.add(new TaskListItem(
							"Mobile Devices > Smartphone > Android OS > SM-G850F (Primary) ",
							"Step changed from [WB] New request in progress to [WPL] Request clarification",
							"Sep 29, 2014 18:04 HKT [10:04 GMT]"));
		}
	}

}
