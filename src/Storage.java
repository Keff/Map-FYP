package com.mad.fyp.tescoolap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class Storage extends Activity {
	String internal, secondary;
	boolean menu = false;
	File cur_dir, internal_sd, secondary_sd;
	ListView lv2;

	// for database usage
	private SQLiteAdapter mySQLiteAdapter;
	AlertDialog.Builder builderWarning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initial setup
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // hide keyboard on startup
		builderWarning = new AlertDialog.Builder(this);
		mySQLiteAdapter = new SQLiteAdapter(this);
		
		// set title of activity
		setTitle("Choose File (*.txt)");

		final LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		lv2 = new ListView(this);
		lv2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		ll.addView(lv2);

		// Internal SD
		internal = System.getenv("EXTERNAL_STORAGE");
		internal_sd = new File(internal);

		// External SD
		secondary = System.getenv("SECONDARY_STORAGE");
		secondary_sd = new File(secondary);

		sdMenu();
		setContentView(ll);
	}

	// browse each folder in the system
	private void viewDir(final String path) {
		cur_dir = new File(path);
		menu = false;

		// always define a new lists once this function is being called,
		// by using clear function the application will force close :(
		final List<String> dir = new ArrayList<String>(); // directory name
		final List<String> file = new ArrayList<String>(); // file name
		final List<String> dir_path = new ArrayList<String>(); // directory path
		final List<String> file_path = new ArrayList<String>(); // file path

		if (cur_dir.isDirectory()) {
			String[] insidefile_name = cur_dir.list(); // list the name of its child
			File[] insidefile_path = cur_dir.listFiles(); // list the path of its child

			if (insidefile_path.length == 0) {
				Toast.makeText(getApplicationContext(),
						"This directory is empty!", Toast.LENGTH_SHORT).show();
			} else {
				// I want to show directory first, then file
				for (int i = 0; i < insidefile_path.length; i++) {
					File chk = new File(insidefile_path[i].toString());

					if (chk.isDirectory()) {
						dir.add(insidefile_name[i]);
						dir_path.add(insidefile_path[i].toString());
					} else if (chk.isFile()) {
						file.add(insidefile_name[i]);
						file_path.add(insidefile_path[i].toString());
					}
				}

				// Sort list first before insert file name
				Collections.sort(dir, new Comparator<String>() {
					@Override
					public int compare(String s1, String s2) {              
						return s1.compareToIgnoreCase(s2);
					}
				});
				Collections.sort(dir_path, new Comparator<String>() {
					@Override
					public int compare(String s1, String s2) {              
						return s1.compareToIgnoreCase(s2);
					}
				});
				Collections.sort(file, new Comparator<String>() {
					@Override
					public int compare(String s1, String s2) {              
						return s1.compareToIgnoreCase(s2);
					}
				});
				Collections.sort(file_path, new Comparator<String>() {
					@Override
					public int compare(String s1, String s2) {              
						return s1.compareToIgnoreCase(s2);
					}
				});

				if (!file.isEmpty()) {					
					dir.addAll(file);
					dir_path.addAll(file_path);		
				}

				ArrayAdapter<String> dir_adapter = new ArrayAdapter<String>(
						this, android.R.layout.simple_list_item_1, dir);
				dir_adapter.setNotifyOnChange(true);
				lv2.setAdapter(dir_adapter);

				lv2.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						// check selected item is file or directory
						// if selected item is file then will not call viewDir()
						File selected = new File(dir_path.get(position));

						if (selected.isDirectory())
							viewDir(dir_path.get(position));
						else if (selected.isFile()) {
							String extension = null;
							int index = dir.get(position).lastIndexOf('.');

							if (index > 0) {
								extension = dir.get(position).substring(index+1);

								if (extension.equals("txt")) {
									int line = 0;
									String content = null;
									boolean havItem = false;

									try {
										BufferedReader br = new BufferedReader(new FileReader(selected));
										mySQLiteAdapter.openToWrite();

										while ((content = br.readLine()) != null) {
											if (line == 0) {
												if (!content.equals("Item name, Quantity, Price per item, Month, Quarter, Year, Town, City, Country")) {
													builderWarning.setTitle("Warning").setIcon(android.R.drawable.ic_dialog_alert)
													.setMessage("Invalid format of data inside text file!")
													.setNeutralButton("Close", null).show();

													break;
												}
											} else if (line > 0) {
												String[] parts = content.split(", ");

												String name = parts[0];
												int quantity = Integer.parseInt(parts[1]);
												double price = Double.parseDouble(parts[2]);
												String month = parts[3];
												String quarter = parts[4];
												int year = Integer.parseInt(parts[5]);
												String town = parts[6];
												String city = parts[7];
												String country = parts[8];

												mySQLiteAdapter.insert(name, quantity, price, month, 
														quarter, year, town, city, country);
												havItem = true;
											}
											
											line++;
										}

										if (havItem) {
											Toast.makeText(getApplicationContext(),
													"You have inserted " + (line-1) + " items into database.", Toast.LENGTH_SHORT).show();
											
											Intent i = new Intent(Storage.this, MainActivity.class);
											startActivity(i);
											finish();
										}

										mySQLiteAdapter.close();
										br.close();
										
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else 
									builderWarning.setTitle("Invalid file").setIcon(android.R.drawable.ic_dialog_alert)
									.setMessage("This is not a text file!")
									.setNeutralButton("Close", null).show();
							} else 
								builderWarning.setTitle("Invalid file").setIcon(android.R.drawable.ic_dialog_alert)
								.setMessage("This is not a text file!")
								.setNeutralButton("Close", null).show();
						}
					}
				});
			}
		}
	}

	private void sdMenu() {
		String[] sd_choice = null;
		menu = true;

		if (internal_sd.listFiles() == null)
			sd_choice = new String[] { "external_sd" };
		else if (secondary_sd.listFiles() == null)
			sd_choice = new String[] { "sdcard" };
		else if (internal_sd.listFiles() != null
				&& secondary_sd.listFiles() != null)
			sd_choice = new String[] { "sdcard", "external_sd" };

		ArrayAdapter<String> select_sd = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, sd_choice);
		select_sd.setNotifyOnChange(true);
		lv2.setAdapter(select_sd);

		lv2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0)
					viewDir(internal);
				else if (position == 1)
					viewDir(secondary);
			}
		});
	}

	// custom back function
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Avoid back key hitting onKeyDown twice
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (!menu) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if ((cur_dir.toString().equals(internal) || cur_dir.toString().equals(secondary)))
						sdMenu();
					else
						viewDir(cur_dir.getParent());
				}
			} else {
				Intent i = new Intent(Storage.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		}

		return false;
	}
}
