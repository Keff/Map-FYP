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
	List<String> result;
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

		if (selectedColumn.equals("Month & Quarter")) {
			setTitle("Roll Up/Down (Month & Quarter)");
			Toast.makeText(getApplicationContext(),
					"Roll Up/Down - Month & Quarter\noperation is chosen.", Toast.LENGTH_SHORT).show();

			result = new ArrayList<String>();
			childItems.clear();

			mySQLiteAdapter.openToRead();
			for (int quarter = 0; quarter < parentItems.size(); quarter++) {
				result = mySQLiteAdapter.getRollDown_Column(selectedColumn, parentItems.get(quarter));

				for (int j = 0; j < result.size(); j++) {
					if (result.get(j).equals("1")) {
						result.remove(j);
						result.add(j, "January");
					} else if (result.get(j).equals("2")) {
						result.remove(j);
						result.add(j, "February");
					} else if (result.get(j).equals("3")) {
						result.remove(j);
						result.add(j, "March");
					} else if (result.get(j).equals("4")) {
						result.remove(j);
						result.add(j, "April");
					} else if (result.get(j).equals("5")) {
						result.remove(j);
						result.add(j, "May");
					} else if (result.get(j).equals("6")) {
						result.remove(j);
						result.add(j, "June");
					} else if (result.get(j).equals("7")) {
						result.remove(j);
						result.add(j, "July");
					} else if (result.get(j).equals("8")) {
						result.remove(j);
						result.add(j, "August");
					} else if (result.get(j).equals("9")) {
						result.remove(j);
						result.add(j, "September");
					} else if (result.get(j).equals("10")) {
						result.remove(j);
						result.add(j, "October");
					} else if (result.get(j).equals("11")) {
						result.remove(j);
						result.add(j, "November");
					} else if (result.get(j).equals("12")) {
						result.remove(j);
						result.add(j, "December");
					}	
				}
				childItems.add(result);
			}
			mySQLiteAdapter.close();

		} else if (selectedColumn.equals("Town & City")) {
			setTitle("Roll Up/Down (Town & City)");
			Toast.makeText(getApplicationContext(),
					"Roll Up/Down - Town & City\noperation is chosen.", Toast.LENGTH_SHORT).show();

			result = new ArrayList<String>();
			childItems.clear();

			mySQLiteAdapter.openToRead();
			for (int city = 0; city < parentItems.size(); city++) {
				result = mySQLiteAdapter.getRollDown_Column(selectedColumn, parentItems.get(city));
				childItems.add(result);
			}
			mySQLiteAdapter.close();

		} else if (selectedColumn.equals("City & Country")) {
			setTitle("Roll Up/Down (City & Country)");
			Toast.makeText(getApplicationContext(),
					"Roll Up/Down - City & Country\noperation is chosen.", Toast.LENGTH_SHORT).show();

			result = new ArrayList<String>();
			childItems.clear();

			mySQLiteAdapter.openToRead();
			for (int country = 0; country < parentItems.size(); country++) {
				result = mySQLiteAdapter.getRollDown_Column(selectedColumn, parentItems.get(country));
				childItems.add(result);
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
				List<String> tempChild = (List<String>) childItems.get(groupPosition);

				mySQLiteAdapter.openToRead();
				result = new ArrayList<String>();
				result = mySQLiteAdapter.getRollDown_Data(selectedColumn, tempChild.get(childPosition));
				mySQLiteAdapter.close();

				String msg = "";
				for (int i = 0; i < result.size(); i++) {
					msg += "\n" + (i+1) + ". " + result.get(i).toString();
				}

				builderRes.setTitle(tempChild.get(childPosition)).setIcon(android.R.drawable.ic_dialog_info)
				.setMessage("Column: \n(Item name, Quantity, Price per item, Month, "
						+ "Quarter, Year, Town, City, Country)\n"
						+ msg).setNeutralButton("Ok", null).show();

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
