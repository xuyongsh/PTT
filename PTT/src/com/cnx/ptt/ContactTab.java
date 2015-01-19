package com.cnx.ptt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class ContactTab extends ContentTab {

	private ListView contact_list;
	private LinearLayout contact_index_list;
	private ContactListAdapter ContactlistItemAdapter;
	private ArrayList<Person> person_list;
	
	private TextView tv_show;
	
	private final String[] indexStr = { "¡ü", "¡î", "A", "B", "C", "D", "E", "F", "G", "H",  
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",  
            "V", "W", "X", "Y", "Z", "#"};
	
	private static HashMap<String, Integer> selector;
	
	public ContactTab(){
		
	}
	
	public void init(LayoutInflater inflater, ViewGroup container,
			int layout_xml){
		super.init(inflater, container, layout_xml);
		
		//set layout view resources
		this.setContact_list((ListView) super.getRoot_view().findViewById(R.id.list_view_contacts));
		this.setContact_index_list((LinearLayout) super.getRoot_view().findViewById(R.id.contact_index_list));
		this.setTv_show((TextView) super.getRoot_view().findViewById(R.id.contact_index_display));
		//hide tv_show by default
		this.getTv_show().setVisibility(View.GONE);
		
		//prepare contact items
		this.PrepareData();
		//append contacts to adapter
		this.GenerateAdapter();
		
		//generate index for contact list
		this.GenerateIndex();
		
		//set adapter for list
		this.getContact_list().setAdapter(this.getContactlistItemAdapter());
	}
	
	private void GenerateIndex(){
		
		final LinearLayout IndexListContainer = this.getContact_index_list();
		final ListView ContatContainer = this.getContact_list();
		
		IndexListContainer.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){
			private boolean isFirst = true;
			
			@Override
			public void onGlobalLayout() {

				if (isFirst) {  
                    isFirst = false;  
                    final int height = IndexListContainer.getMeasuredHeight();
                    LinearLayout.LayoutParams params = new LayoutParams(
                    		LayoutParams.WRAP_CONTENT,
                    		height/indexStr.length);
                    
                    for(int i=0; i< indexStr.length; i++){
            			final TextView tv = new TextView(IndexListContainer.getContext()); 
            			tv.setLayoutParams(params);
            			tv.setTextSize(9);
            			tv.setText(indexStr[i]);
            			tv.setPadding(10, 0, 10, 0);
            			
            			IndexListContainer.addView(tv);
            			
            			IndexListContainer.setOnTouchListener(new OnTouchListener(){
            				
            				@Override
            				public boolean onTouch(View v, MotionEvent event) {
            					float y = event.getY();
            					int index = (int) ((y / height) * indexStr.length);
            					
            					if (index > -1 && index < indexStr.length) {
            						String key = indexStr[index];
            						IndexListContainer.setBackgroundColor(Color.GRAY);
            						if (selector.containsKey(key)) {
            							int pos = selector.get(key);
            							if (ContatContainer.getHeaderViewsCount() > 0) {
            								ContatContainer.setSelectionFromTop(
            										pos + ContatContainer.getHeaderViewsCount(), 
            										0);
            							}
            							else{
            								ContatContainer.setSelectionFromTop(
            										pos, 
            										0);
            							}
            						}
            						
            						tv_show.setVisibility(View.VISIBLE);
        							tv_show.setText(indexStr[index]);
            					}
            					
            					switch (event.getAction()) {
            					case MotionEvent.ACTION_DOWN:
            						break;
            					case MotionEvent.ACTION_MOVE:
            						break;
            					case MotionEvent.ACTION_UP:
            						tv_show.setVisibility(View.GONE);
            						IndexListContainer.setBackgroundColor(Color.TRANSPARENT);
            						break;
            					}
            					
            					return true;
            				}
            			});
            		}
                    
				}
				
			}
			
		});
	}
	
	private void PrepareData()
	{		
		ArrayList<Person> persons = new ArrayList<Person>();
		
		// get person data for contact list
		
		persons.add(new Person("Theo Wang", true));
		persons.add(new Person("Levine Li", true));
		persons.add(new Person("Lawrence Wong", true));
		persons.add(new Person("Alan Cao", true));
		persons.add(new Person("Lois Teo", true));
		persons.add(new Person("Teerathut Lerstsakwimand ", true));
		persons.add(new Person("Robin Li", true));
		persons.add(new Person("Andy Wang", true));
		persons.add(new Person("Ryan Shang", true));
		persons.add(new Person("Cindy Zhao", true));
		persons.add(new Person("Madhulika Mohan", true));
		persons.add(new Person("Gilbert Han", true));
		persons.add(new Person("David Xu", true));
		persons.add(new Person("Laura Liu", true));
		persons.add(new Person("Sky Li", true));
		
		//Sorter by name for person objects, only support English name
		class SortByName implements Comparator <Person>{
			
			@Override
			public int compare(Person lhs,
					Person rhs) {
				
				String name1 = (String) lhs.getName();
				String name2 = (String) rhs.getName();
				
				return name1.compareTo(name2);
			}
			
		}
		
		//Add index labors
		ArrayList <String> person_index = new ArrayList<String>();
		for (int j = 0; j < indexStr.length; j++) {
            for (int i = 0; i < persons.size(); i++) {
            	if (
                		!person_index.contains(indexStr[j])
                		&& persons.get(i).getName().substring(0,1).equals(indexStr[j]) 
                		){
            		person_index.add(indexStr[j]);
            	}
            }
		}
		
		for(int i = 0; i< person_index.size(); i++)
		{
			persons.add(new Person(person_index.get(i), false));
		}
		
		// Sort by name
		Collections.sort(persons, new SortByName());
		
		//Index persons
		selector = new HashMap<String, Integer>();
		//TODO: poor performance, three loops here...
		for (int j = 0; j < indexStr.length; j++) {
            for (int i = 0; i < persons.size(); i++) {

            	// setup selector for index
                if (
                		!selector.containsKey(indexStr[j])
                		&& persons.get(i).getName().substring(0,1).equals(indexStr[j]) 
                		) {
                    selector.put(indexStr[j], i);  
                }  
            }
		}

		this.setPerson_list(persons);
		
	}
	
	private void GenerateAdapter()
	{
		this.setContactlistItemAdapter(new ContactListAdapter(
				this.getContact_list().getContext(), this.getPerson_list()));
	}

	public ListView getContact_list() {
		return contact_list;
	}

	public void setContact_list(ListView contact_list) {
		this.contact_list = contact_list;
	}

	public LinearLayout getContact_index_list() {
		return contact_index_list;
	}

	public void setContact_index_list(LinearLayout contact_index_list) {
		this.contact_index_list = contact_index_list;
	}

	public ContactListAdapter getContactlistItemAdapter() {
		return ContactlistItemAdapter;
	}

	public void setContactlistItemAdapter(ContactListAdapter contactlistItemAdapter) {
		ContactlistItemAdapter = contactlistItemAdapter;
	}

	public TextView getTv_show() {
		return tv_show;
	}

	public void setTv_show(TextView tv_show) {
		this.tv_show = tv_show;
	}

	public ArrayList<Person> getPerson_list() {
		return person_list;
	}

	public void setPerson_list(ArrayList<Person> person_list) {
		this.person_list = person_list;
	}
}
