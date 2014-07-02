package com.cnx.ptt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

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
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ContactTab extends ContentTab {

	private ListView contact_list;
	private LinearLayout contact_index_list;
	private ArrayList<HashMap<String, Object>> contactItems;
	private SimpleAdapter ContactlistItemAdapter;
	
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
		
		//create contact items container
		this.setContactItems(new ArrayList<HashMap<String, Object>>());
		
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
		
		persons.add(new Person("Theo Wang"));
		persons.add(new Person("Levine Li"));
		persons.add(new Person("Lawrence Wong"));
		persons.add(new Person("Alan Cao"));
		persons.add(new Person("Lois Teo"));
		persons.add(new Person("Teerathut Lerstsakwimand "));
		persons.add(new Person("Robin Li"));
		persons.add(new Person("Andy Wang"));
		persons.add(new Person("Ryan Shang"));
		persons.add(new Person("Cindy Zhao"));
		persons.add(new Person("Madhulika Mohan"));
		persons.add(new Person("Gilbert Han"));
		persons.add(new Person("David Xu"));
		persons.add(new Person("Laura Liu"));
		persons.add(new Person("Sky Li"));
		
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
		
		// Sort by name
		Collections.sort(persons, new SortByName());
		
		//Index persons
		selector = new HashMap<String, Integer>();
		for (int j = 0; j < indexStr.length; j++) {
            for (int i = 0; i < persons.size(); i++) {
            	
            	if(j == 0){
            		//Prepare data for simple adapter
            		//TODO: Add index indicator on view
            		HashMap<String, Object> map = new HashMap<String, Object>();
        			map.put("ItemImage", R.drawable.ic_launcher);
        			map.put("ItemName", persons.get(i).getName());
        			this.getContactItems().add(map);
            	}
            	
            	
            	
                if (persons.get(i).getName().substring(0,1).equals(indexStr[j])) {
                	//TODO: now the selector position will be overwrite by the last name.
                	//Need to only put the first name of each index
                    selector.put(indexStr[j], i);  
                }  
            }
		}

		
	}
	
	private void GenerateAdapter()
	{
		this.setContactlistItemAdapter(new SimpleAdapter(
				this.getContact_list().getContext(), 
				this.getContactItems(), 
				R.layout.contact_item, 
				new String[] {"ItemName"},
				new int[] {R.id.contact_person_name}));
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

	public SimpleAdapter getContactlistItemAdapter() {
		return ContactlistItemAdapter;
	}

	public void setContactlistItemAdapter(SimpleAdapter contactlistItemAdapter) {
		ContactlistItemAdapter = contactlistItemAdapter;
	}

	public ArrayList<HashMap<String, Object>> getContactItems() {
		return contactItems;
	}

	public void setContactItems(ArrayList<HashMap<String, Object>> contactItems) {
		this.contactItems = contactItems;
	}

	public TextView getTv_show() {
		return tv_show;
	}

	public void setTv_show(TextView tv_show) {
		this.tv_show = tv_show;
	}
}
