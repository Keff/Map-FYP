package com.mad.fyp.tescoolap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class RollUp extends Activity {
	
	// for database usage
	private SQLiteAdapter mySQLiteAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initial setup
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // hide keyboard on startup
		
		Intent i = this.getIntent();
		final String selectedColumn = i.getStringExtra("column");
		
		mySQLiteAdapter = new SQLiteAdapter(this);
		mySQLiteAdapter.openToRead();
		
		List<String> col = new ArrayList<String>();
		col = mySQLiteAdapter.getRollUp_Column(selectedColumn);
		mySQLiteAdapter.close();
		
		final ScrollView VerticalSV = new ScrollView(this);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView tvColumnNames = new TextView(this);
		tvColumnNames.setTypeface(Typeface.SERIF, Typeface.ITALIC);
		ll.addView(tvColumnNames);
		
		if (selectedColumn.equals("Month -> Quarter")) {
			setTitle("Roll Up ( Month -> Quarter )");
			Toast.makeText(getApplicationContext(),
					"You have chosen Roll Up (Month -> Quarter)", Toast.LENGTH_SHORT).show();
			tvColumnNames.setText("Column Name: \n(Item name, Quantity, Price per item, Month, Year, Town, City, Country)\n");
			
			List<TextView> tvsQuarter = new ArrayList<TextView>();
			List<String> result = new ArrayList<String>();

			mySQLiteAdapter.openToRead();
			for (int quarter = 0; quarter < col.size(); quarter++) {
				TextView tvQuarter = new TextView(this);
				tvQuarter.setTypeface(null, Typeface.BOLD_ITALIC);
				
				if (col.get(quarter).equals("Q1"))
					tvQuarter.append("Quarter 1");
				else if (col.get(quarter).equals("Q2"))
					tvQuarter.append("Quarter 2");
				else if (col.get(quarter).equals("Q3"))
					tvQuarter.append("Quarter 3");
				else if (col.get(quarter).equals("Q4"))
					tvQuarter.append("Quarter 4");
				
				tvsQuarter.add(tvQuarter);
				ll.addView(tvsQuarter.get(quarter));

				// finally I get it, linear layout cannot add same list with same value 
				List<TextView> tvsResult = new ArrayList<TextView>();
				result = mySQLiteAdapter.getRollUp_Data(selectedColumn, col.get(quarter));
				
				for (int e = 0; e < result.size(); e++) {
					TextView tvResult = new TextView(this);
					tvResult.append((e + 1) + " " + result.get(e));
					if (e == result.size()-1)
						tvResult.append("\n");
					tvResult.setTextSize(14);
				
					tvsResult.add(tvResult);
					ll.addView(tvsResult.get(e));
				}
			}
			mySQLiteAdapter.close();
			
		} else if (selectedColumn.equals("Town -> City")) {
			setTitle("Roll Up ( Town -> City )");
			Toast.makeText(getApplicationContext(),
					"You have chosen Roll Up (Town -> City)", Toast.LENGTH_SHORT).show();
			tvColumnNames.setText("Column Name: \n(Item name, Quantity, Price per item, Month, Quarter, Year, Town, Country)\n");
			
			List<TextView> tvsCity = new ArrayList<TextView>();
			List<String> result = new ArrayList<String>();

			mySQLiteAdapter.openToRead();
			for (int city = 0; city < col.size(); city++) {
				TextView tvCity = new TextView(this);
				tvCity.setTypeface(null, Typeface.BOLD_ITALIC);
				tvCity.append(col.get(city));
				tvsCity.add(tvCity);
				ll.addView(tvsCity.get(city));

				// finally I get it, linear layout cannot add same list with same value 
				List<TextView> tvsResult = new ArrayList<TextView>();
				result = mySQLiteAdapter.getRollUp_Data(selectedColumn, col.get(city));
				
				for (int e = 0; e < result.size(); e++) {
					TextView tvResult = new TextView(this);
					tvResult.append((e + 1) + " " + result.get(e));
					if (e == result.size()-1)
						tvResult.append("\n");
					tvResult.setTextSize(14);
				
					tvsResult.add(tvResult);
					ll.addView(tvsResult.get(e));
				}		
			}
			mySQLiteAdapter.close();
			
		} else if (selectedColumn.equals("City -> Country")) {
			setTitle("Roll Up ( City -> Country )");
			Toast.makeText(getApplicationContext(),
					"You have chosen Roll Up (City -> Country)", Toast.LENGTH_SHORT).show();
			tvColumnNames.setText("Column Name: \n(Item name, Quantity, Price per item, Month, Quarter, Year, Town, City)\n");
			
			List<TextView> tvsCountry = new ArrayList<TextView>();
			List<String> result = new ArrayList<String>();

			mySQLiteAdapter.openToRead();
			for (int country = 0; country < col.size(); country++) {
				TextView tvCountry = new TextView(this);
				tvCountry.setTypeface(null, Typeface.BOLD_ITALIC);
				tvCountry.append(col.get(country));
				tvsCountry.add(tvCountry);
				ll.addView(tvsCountry.get(country));

				// finally I get it, linear layout cannot add same list with same value 
				List<TextView> tvsResult = new ArrayList<TextView>();
				result = mySQLiteAdapter.getRollUp_Data(selectedColumn, col.get(country));
				
				for (int f = 0; f < result.size(); f++) {
					TextView tvResult = new TextView(this);
					tvResult.append((f + 1) + " " + result.get(f));
					if (f == result.size()-1)
						tvResult.append("\n");
					tvResult.setTextSize(14);
				
					tvsResult.add(tvResult);
					ll.addView(tvsResult.get(f));
				}		
			}
			mySQLiteAdapter.close();
		}

		VerticalSV.addView(ll);
		setContentView(VerticalSV);
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
