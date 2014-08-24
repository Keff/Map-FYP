package com.mad.fyp.tescoolap;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class RollUp extends ExpandableListActivity implements OnChildClickListener {
	
	// for database usage
	private SQLiteAdapter mySQLiteAdapter;
	List<List<String>> result;
	List<String> parentItems;
	List<Object> childItems;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = this.getIntent();
		final String selectedColumn = i.getStringExtra("column");
		
		mySQLiteAdapter = new SQLiteAdapter(this);
		mySQLiteAdapter.openToRead();
		
		parentItems = new ArrayList<String>();
		childItems = new ArrayList<Object>();
		parentItems = mySQLiteAdapter.getRollUp_Column(selectedColumn);
		mySQLiteAdapter.close();

		 // Create Expandable List and set it's properties
        ExpandableListView expandableList = getExpandableListView(); 
        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);

		if (selectedColumn.equals("Month -> Quarter")) {
			setTitle("Roll Up ( Month -> Quarter )");
			Toast.makeText(getApplicationContext(),
					"You have chosen Roll Up (Month -> Quarter)", Toast.LENGTH_SHORT).show();
			
			result = new ArrayList<List<String>>();

			mySQLiteAdapter.openToRead();
			for (int quarter = 0; quarter < parentItems.size(); quarter++) {
				result = mySQLiteAdapter.getRollUp_Data(selectedColumn, parentItems.get(quarter));
				childItems.add(result.get(0));
			}
			mySQLiteAdapter.close();
			
		} else if (selectedColumn.equals("Town -> City")) {
			setTitle("Roll Up ( Town -> City )");
			Toast.makeText(getApplicationContext(),
					"You have chosen Roll Up (Town -> City)", Toast.LENGTH_SHORT).show();

			result = new ArrayList<List<String>>();

			mySQLiteAdapter.openToRead();
			for (int city = 0; city < parentItems.size(); city++) {
				result = mySQLiteAdapter.getRollUp_Data(selectedColumn, parentItems.get(city));
				childItems.add(result.get(0));
			}
			mySQLiteAdapter.close();
			
		} else if (selectedColumn.equals("City -> Country")) {
			setTitle("Roll Up ( City -> Country )");
			Toast.makeText(getApplicationContext(),
					"You have chosen Roll Up (City -> Country)", Toast.LENGTH_SHORT).show();

			result = new ArrayList<List<String>>();

			mySQLiteAdapter.openToRead();
			for (int country = 0; country < parentItems.size(); country++) {
				result = mySQLiteAdapter.getRollUp_Data(selectedColumn, parentItems.get(country));
				childItems.add(result.get(0));
			}
			mySQLiteAdapter.close();
		}

		OlapExpandableAdapter expandAdapter = new OlapExpandableAdapter(parentItems, childItems);
		expandAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
		getExpandableListView().setAdapter(expandAdapter);
		
		final AlertDialog.Builder builderRes = new AlertDialog.Builder(this);	
		expandableList.setOnChildClickListener(new OnChildClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				if (selectedColumn.equals("Month -> Quarter")) {
					mySQLiteAdapter.openToRead();
					result = mySQLiteAdapter.getRollUp_Data(selectedColumn, parentItems.get(groupPosition));
					mySQLiteAdapter.close();

					List<String> res = new ArrayList<String>();
					res.addAll(result.get(1));

					String msg = "";
					for (int i = 0; i < res.size(); i++) {
						msg += (i+1) + ". " + res.get(i).toString();
					}

					builderRes.setTitle(parentItems.get(groupPosition)).setIcon(android.R.drawable.ic_dialog_info)
					.setMessage("Column Name: \n(Item name, Quantity, Price per item, Month, Year, Town, City, Country)\n\n"
							+ msg).setNeutralButton("Ok", null).show();
				} else if (selectedColumn.equals("Town -> City")) {
					List<String> tempChild = (List<String>) childItems.get(groupPosition);

					builderRes.setTitle(parentItems.get(groupPosition)).setIcon(android.R.drawable.ic_dialog_info)
					.setMessage("Column Name: \n(Item name, Quantity, Price per item, Month, Quarter, Year, Town, Country)\n\n"
							+ tempChild.get(childPosition)).setNeutralButton("Ok", null).show();
				} else if (selectedColumn.equals("City -> Country")) {
					List<String> tempChild = (List<String>) childItems.get(groupPosition);

					builderRes.setTitle(parentItems.get(groupPosition)).setIcon(android.R.drawable.ic_dialog_info)
					.setMessage("Column Name: \n(Item name, Quantity, Price per item, Month, Quarter, Year, Town, City)\n\n"
							+ tempChild.get(childPosition)).setNeutralButton("Ok", null).show();
				}

				return false;
			}
        });
	}

	// own 'Back" function.. more accurate and efficiently
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			onBackPressed();
		}

		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() {
		Intent i = new Intent(RollUp.this, MainActivity.class);
		startActivity(i);
		finish();

		return;
	}  
}
