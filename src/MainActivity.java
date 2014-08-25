package com.mad.fyp.tescoolap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// for database usage
	private SQLiteAdapter mySQLiteAdapter;
	int count = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* initial setup !!! */
		// call image to be load
		ImageView image = new ImageView(this);
		image.setImageResource(R.drawable.tesco);

		mySQLiteAdapter = new SQLiteAdapter(this);
		mySQLiteAdapter.openToRead();
		count = mySQLiteAdapter.count();
		mySQLiteAdapter.close();

		/* end of initial setup */

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		Button btDirectory = new Button(this);
		btDirectory.setText("Choose File");

		final Button btOlap = new Button(this);
		btOlap.setText("Olap Operation");

		final Button btDeletedb = new Button(this);
		btDeletedb.setText("Database Reset");

		final TextView tvDBStatus = new TextView(this);
		tvDBStatus.setText("\n\nCurrent Database: " + count + " item(s)");
		tvDBStatus.setTypeface(null, Typeface.BOLD_ITALIC);
		tvDBStatus.setGravity(Gravity.RIGHT);

		if (count == 0) {
			btOlap.setEnabled(false);
			btDeletedb.setEnabled(false);
		} else {
			btOlap.setEnabled(true);
			btDeletedb.setEnabled(true);
		}

		btDirectory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, Storage.class);				
				startActivity(i);
				finish();
			}
		});

		btOlap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				olapOperation();
			}

		});

		final AlertDialog.Builder builderDeletedb = new AlertDialog.Builder(this);
		btDeletedb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builderDeletedb.setTitle("Reset Database?").setIcon(android.R.drawable.ic_menu_delete)
				.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mySQLiteAdapter.openToWrite();
						mySQLiteAdapter.deleteAll();
						count = mySQLiteAdapter.count();
						mySQLiteAdapter.close();

						// update the interface elements
						btOlap.setEnabled(false);
						btDeletedb.setEnabled(false);
						tvDBStatus.setText("\n\nCurrent Database: " + count + " item(s)");

						Toast.makeText(getApplicationContext(),
								"Database is cleaned!!", Toast.LENGTH_SHORT).show();
					}
				}).setNeutralButton("Cancel", null).show();
			}});

		ll.addView(image);
		ll.addView(btDirectory);
		ll.addView(btOlap);
		ll.addView(btDeletedb);
		ll.addView(tvDBStatus);
		setContentView(ll);
	}

	private void olapOperation() {
		// TODO Auto-generated method stub
		TextView tvMsg = new TextView(this);
		tvMsg.setText("Select an Olap Operation.");

		final String[] olapOperation = {"...", "Roll Up & Down", "Slice & Dice", "Pivot (rotate)"};
		ArrayAdapter<String> adapterOlapOperation = 
				new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, olapOperation);
		adapterOlapOperation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		final Spinner spOlapOperation = new Spinner(this);
		spOlapOperation.setAdapter(adapterOlapOperation);

		final LinearLayout llOlap = new LinearLayout(this);
		llOlap.setOrientation(LinearLayout.VERTICAL);
		llOlap.addView(tvMsg);
		llOlap.addView(spOlapOperation);

		// initialize views to use on setOnItemSelectedListener later
		final TextView tvMsg2 = new TextView(this);
		final List<String> lstColumn = new ArrayList<String>();
		final ArrayAdapter<String> adapterColumn = 
				new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, lstColumn);
		adapterColumn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner spColumn = new Spinner(this);

		// allows user to choose which column to perform their desired OLAP operation
		spOlapOperation.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				lstColumn.clear();
				llOlap.removeView(tvMsg2);
				llOlap.removeView(spColumn);

				if (position == 1) {
					tvMsg2.setText("\nSelect column for Roll Up & Down operation.");
					lstColumn.add("Month & Quarter");
					lstColumn.add("Town & City");
					lstColumn.add("City & Country");

					llOlap.addView(tvMsg2);
					llOlap.addView(spColumn);
				} else if (position == 2) {
					tvMsg2.setText("\nSelect column for Slice & Dice operation.");

					llOlap.addView(tvMsg2);
				} else if (position == 3) {
					tvMsg2.setText("\nSelect column for Pivot (rotate) operation.");

					llOlap.addView(tvMsg2);
				}

				spColumn.setAdapter(adapterColumn);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}});

		final AlertDialog.Builder builderOlap = new AlertDialog.Builder(this);	
		builderOlap.setTitle("Olap Operation").setIcon(android.R.drawable.ic_dialog_info).setView(llOlap)
		.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String selectedOlapOperation = spOlapOperation.getSelectedItem().toString();
				String selectedColumn = null;

				if (!(selectedOlapOperation.equals(olapOperation[0].toString()) || selectedOlapOperation.equals(olapOperation[2].toString())
						|| selectedOlapOperation.equals(olapOperation[3].toString())))
					selectedColumn = spColumn.getSelectedItem().toString();

				// Select ..., no operation is chosen
				if (selectedOlapOperation.equals(olapOperation[0].toString())) {
					Toast.makeText(getApplicationContext(),
							"You must choose an Olap Operation.", Toast.LENGTH_SHORT).show();
				} 
				// Roll Up and Down Operation
				else if (selectedOlapOperation.equals(olapOperation[1].toString())) {					
					Intent i = new Intent(MainActivity.this, RollUp.class);	
					i.putExtra("column", selectedColumn);
					startActivity(i);
					finish();
				} /*else if (selectedOlapOperation.equals(olapOperation[2].toString())) {
					Intent i = new Intent(MainActivity.this, RollDown.class);	
					i.putExtra("column", selectedColumn);
					startActivity(i);
					finish();
				} else if (selectedOlapOperation.equals(olapOperation[3].toString())) {
					Intent i = new Intent(MainActivity.this, Slice.class);	
					i.putExtra("column", selectedColumn);
					startActivity(i);
					finish();
				}*/	
			}
		}).setNeutralButton("Cancel", null).show();
	}
}
