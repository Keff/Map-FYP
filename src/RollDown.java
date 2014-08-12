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

public class RollDown extends Activity {
	
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
		col = mySQLiteAdapter.getRollDown_Column(selectedColumn);
		mySQLiteAdapter.close();
		
		final ScrollView VerticalSV = new ScrollView(this);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView tvColumnNames = new TextView(this);
		tvColumnNames.setTypeface(Typeface.SERIF, Typeface.ITALIC);
		ll.addView(tvColumnNames);
		
		if (selectedColumn.equals("Quarter -> Month")) {
			setTitle("Roll Down ( Quarter -> Month )");
			Toast.makeText(getApplicationContext(),
					"You have chosen Roll Down ( Quarter -> Month )", Toast.LENGTH_SHORT).show();
			tvColumnNames.setText("Column Name: \n(Item name, Quantity, Price per item, Quarter, Year, Town, City, Country)\n");
			
			List<TextView> tvsMonth = new ArrayList<TextView>();
			List<String> result = new ArrayList<String>();

			mySQLiteAdapter.openToRead();
			for (int month = 0; month < col.size(); month++) {
				TextView tvQuarter = new TextView(this);
				tvQuarter.setTypeface(null, Typeface.BOLD_ITALIC);
				
				if (col.get(month).equals("1 - January"))
					tvQuarter.append("January");
				else if (col.get(month).equals("2 - February"))
					tvQuarter.append("February");
				else if (col.get(month).equals("3 - March"))
					tvQuarter.append("March");
				else if (col.get(month).equals("4 - April"))
					tvQuarter.append("April");
				else if (col.get(month).equals("5 - May"))
					tvQuarter.append("May");
				else if (col.get(month).equals("6 - June"))
					tvQuarter.append("June");
				else if (col.get(month).equals("7 - July"))
					tvQuarter.append("July");
				else if (col.get(month).equals("8 - August"))
					tvQuarter.append("August");
				else if (col.get(month).equals("9 - September"))
					tvQuarter.append("September");
				else if (col.get(month).equals("10 - October"))
					tvQuarter.append("October");
				else if (col.get(month).equals("11 - November"))
					tvQuarter.append("November");
				else if (col.get(month).equals("12 - December"))
					tvQuarter.append("December");
				
				tvsMonth.add(tvQuarter);
				ll.addView(tvsMonth.get(month));

				// finally I get it, linear layout cannot add same list with same value 
				List<TextView> tvsResult = new ArrayList<TextView>();
				result = mySQLiteAdapter.getRollDown_Data(selectedColumn, col.get(month));
				
				for (int e = 0; e < result.size(); e++) {
					TextView tvResult = new TextView(this);
					tvResult.append((e + 1) + ". " + result.get(e));
					if (e == result.size()-1)
						tvResult.append("\n");
					tvResult.setTextSize(14);
				
					tvsResult.add(tvResult);
					ll.addView(tvsResult.get(e));
				}
			}
			mySQLiteAdapter.close();
			
		} else if (selectedColumn.equals("Country -> City")) {
			setTitle("Roll Down ( Country -> City )");
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
				result = mySQLiteAdapter.getRollDown_Data(selectedColumn, col.get(city));
				
				for (int e = 0; e < result.size(); e++) {
					TextView tvResult = new TextView(this);
					tvResult.append((e + 1) + ". " + result.get(e));
					if (e == result.size()-1)
						tvResult.append("\n");
					tvResult.setTextSize(14);
				
					tvsResult.add(tvResult);
					ll.addView(tvsResult.get(e));
				}		
			}
			mySQLiteAdapter.close();
			
		} else if (selectedColumn.equals("City -> Town")) {
			setTitle("Roll Down ( City -> Town )");
			Toast.makeText(getApplicationContext(),
					"You have chosen Roll Down ( City -> Town )", Toast.LENGTH_SHORT).show();
			tvColumnNames.setText("Column Name: \n(Item name, Quantity, Price per item, Month, Quarter, Year, City, Country)\n");
			
			List<TextView> tvsTown = new ArrayList<TextView>();
			List<String> result = new ArrayList<String>();

			mySQLiteAdapter.openToRead();
			for (int town = 0; town < col.size(); town++) {
				TextView tvTown = new TextView(this);
				tvTown.setTypeface(null, Typeface.BOLD_ITALIC);
				tvTown.append(col.get(town));
				tvsTown.add(tvTown);
				ll.addView(tvsTown.get(town));

				// finally I get it, linear layout cannot add same list with same value 
				List<TextView> tvsResult = new ArrayList<TextView>();
				result = mySQLiteAdapter.getRollDown_Data(selectedColumn, col.get(town));
				
				for (int f = 0; f < result.size(); f++) {
					TextView tvResult = new TextView(this);
					tvResult.append((f + 1) + ". " + result.get(f));
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
		Intent i = new Intent(RollDown.this, MainActivity.class);
		startActivity(i);
		finish();

		return;
	}  
}
