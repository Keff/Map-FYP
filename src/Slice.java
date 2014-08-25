package com.mad.fyp.tescoolap;

import java.util.ArrayList;
import java.util.List;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class Slice extends ExpandableListActivity implements OnChildClickListener {

	// for database usage
	private SQLiteAdapter mySQLiteAdapter;
	List<String> result;
	List<String> parentItems;
	List<Object> childItems;
	String selectedColumn, selectedColumnChild;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		parentItems = new ArrayList<String>();
		childItems = new ArrayList<Object>();

		Intent i = this.getIntent();
		parentItems.add(0, i.getStringExtra("columnChild").toString());

		mySQLiteAdapter = new SQLiteAdapter(this);
		mySQLiteAdapter.openToRead();
		childItems = mySQLiteAdapter.getSliceData(i.getStringExtra("column").toString(), parentItems.get(0).toString());
		mySQLiteAdapter.close();

		// Create Expandable List and set it's properties
		ExpandableListView expandableList = getExpandableListView(); 
		expandableList.setDividerHeight(2);
		expandableList.setGroupIndicator(null);
		expandableList.setClickable(true);

		OlapExpandableAdapter expandAdapter = new OlapExpandableAdapter(parentItems, childItems);
		expandAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
		getExpandableListView().setAdapter(expandAdapter);

		// Expand them by default
		for(int j = 0; j < expandAdapter.getGroupCount(); j++)
			expandableList.expandGroup(j);
	}

	// Defined to-do function to menu buttons
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		OnMenuItemClickListener homeListener = new OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();

				return false;
			}
		};

		menu.add("Return to home").setOnMenuItemClickListener(homeListener);

		return true;
	}

	// Defined to-do function to back buttons
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			onBackPressed();
		}

		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() {
		Intent i = new Intent(Slice.this, MainActivity.class);
		startActivity(i);
		finish();

		return;
	}
}
