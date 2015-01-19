package com.cnx.ptt;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

public class ExploreTab extends ContentTab {
	
	private ArrayList <ExploreModule> modules;
	private ExploreModuleListAdapter emlistAdapter;
	private ListView listView;
	
	public static final int POSITION_TIMETRACK = 0;
	public static final int POSITION_TIMESHEETTODAY = 1;
	public static final int POSITION_TASKMORNITOR = 2;
	
	public void init(LayoutInflater inflater, ViewGroup container, int layout_xml){
		super.init(inflater, container, layout_xml);
		this.setListView((ListView) super.getRoot_view().findViewById(R.id.explore_module_list));
		
		this.loadModules();
		this.genAdapter();
		
		this.getListView().setAdapter(this.getEmlistAdapter());
	}
	
	private void loadModules(){
		this.setModules(new ArrayList<ExploreModule>());
		this.getModules().add(
				new ExploreModule(
						R.layout.explore_module_item, 
						R.drawable.ic_action_alarms, 
						R.string.track_time
						));
		
		this.getModules().add(
				new ExploreModule(
						R.layout.explore_module_item, 
						R.drawable.ic_action_go_to_today, 
						R.string.time_sheet_today
						));
		
		this.getModules().add(
				new ExploreModule(
						R.layout.explore_module_item, 
						R.drawable.ic_action_important, 
						R.string.task_mornitor
						));
	}
	
	private void genAdapter(){
		ExploreModuleListAdapter emla = new ExploreModuleListAdapter(
				this.getListView().getContext(),
				this.getModules());
		this.setEmlistAdapter(emla);
		
	}

	public ArrayList <ExploreModule> getModules() {
		return modules;
	}

	public void setModules(ArrayList <ExploreModule> modules) {
		this.modules = modules;
	}

	public ExploreModuleListAdapter getEmlistAdapter() {
		return emlistAdapter;
	}

	public void setEmlistAdapter(ExploreModuleListAdapter emlistAdapter) {
		this.emlistAdapter = emlistAdapter;
	}

	public ListView getListView() {
		return listView;
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}
}
