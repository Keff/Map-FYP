package com.mad.fyp.tescoolap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
	int count = 0, line = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* initial setup !!! */
		// call image to be load
		ImageView image = new ImageView(this);
		image.setImageResource(R.drawable.tesco);
		final AlertDialog.Builder builderInternetAccess = new AlertDialog.Builder(this);
		final AlertDialog.Builder builderDeletedb = new AlertDialog.Builder(this);

		mySQLiteAdapter = new SQLiteAdapter(this);
		mySQLiteAdapter.openToRead();
		count = mySQLiteAdapter.count();
		mySQLiteAdapter.close();

		/* end of initial setup */

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		final Button btServer = new Button(this);
		btServer.setText("Download from Server");

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

		btServer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Get connection info
				ConnectivityManager cm =
						(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cm.getActiveNetworkInfo();

				if (netInfo != null && netInfo.isConnected()) {
					new readFromServer().execute("http://download1648.mediafire.com/0f27je35elvg/8pfl8h74184l4oj/Ikan.txt");
				} else {
					builderInternetAccess.setTitle("Warning").setMessage(
							"No internet access!").setIcon(android.R.drawable.ic_dialog_alert).setNeutralButton(
									"Close", null).show();
				}
			}});

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
		ll.addView(btServer);
		ll.addView(btDirectory);
		ll.addView(btOlap);
		ll.addView(btDeletedb);
		ll.addView(tvDBStatus);
		setContentView(ll);
	}

	private class readFromServer extends AsyncTask<String, Void, String> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			// show progress dialog when downloading 
			dialog = ProgressDialog.show(MainActivity.this, null, "Downloading...");
		}

		@Override
		protected String doInBackground(String... params) {

			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(params[0]);
				HttpResponse response = httpClient.execute(httpGet);
				HttpEntity entity = response.getEntity();

				BufferedHttpEntity buf = new BufferedHttpEntity(entity);
				InputStream is = buf.getContent();
				BufferedReader r = new BufferedReader(new InputStreamReader(is));

				StringBuilder total = new StringBuilder();
				String content = "";

				mySQLiteAdapter.openToWrite();
				while ((content = r.readLine()) != null) {
					total.append(content + "\n");

					if (line == 0) {
						if (!content.equals("Item name, Quantity, Price per item, Month, Quarter, Year, Town, City, Country"))
							break;

					} else if (line > 0) {
						String[] parts = content.split(", ");

						String name = parts[0];
						int quantity = Integer.parseInt(parts[1]);
						String price = parts[2];
						String month = parts[3];
						String quarter = parts[4];
						int year = Integer.parseInt(parts[5]);
						String town = parts[6];
						String city = parts[7];
						String country = parts[8];

						mySQLiteAdapter.insert(name, quantity, price, month, 
								quarter, year, town, city, country);
					}

					line++;
				}

				mySQLiteAdapter.close();
				r.close();

				String result = total.toString();
				Log.i("Get URL", "Downloaded string: " + result);
				return result;
			} catch (Exception e) {
				Log.e("Get Url", "Error in downloading: " + e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// TODO change text view id for yourself
			// close progresses dialog
			dialog.dismiss();

			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();

			Toast.makeText(getApplicationContext(),
					"Download Complete.\n" + (line-1) + " items inserted.", Toast.LENGTH_SHORT).show();
		}
	}

	private void olapOperation() {
		// TODO Auto-generated method stub
		final TextView tvMsg = new TextView(this);
		final TextView tvMsg2 = new TextView(this);
		final TextView tvMsg3 = new TextView(this);
		tvMsg.setText("Select an Olap Operation.");

		final String[] olapOperation = {"...", "Drill Up & Down", "Slice & Dice"};
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
		final List<String> lstColumn = new ArrayList<String>();		
		final ArrayAdapter<String> adapterColumn = 
				new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, lstColumn);
		adapterColumn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner spColumn = new Spinner(this);

		final List<String> lstColumnChild = new ArrayList<String>();
		final ArrayAdapter<String> adapterColumnChild = 
				new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, lstColumnChild);
		adapterColumnChild.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner spColumnChild = new Spinner(this);

		// allows user to choose which column to perform their desired OLAP operation
		spOlapOperation.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				lstColumn.clear();
				llOlap.removeView(tvMsg2);
				llOlap.removeView(spColumn);

				// Disable the annoying listener called when the user not choosing Slice and Dice operation
				lstColumnChild.clear();
				llOlap.removeView(tvMsg3);
				llOlap.removeView(spColumnChild);
				spColumn.setOnItemSelectedListener(null);

				if (position == 1) {
					tvMsg2.setText("\nSelect column type for Drill Up & Down operation.");
					lstColumn.add("Month & Quarter");
					lstColumn.add("Town & City");
					lstColumn.add("City & Country");

					llOlap.addView(tvMsg2);
					llOlap.addView(spColumn);
				} else if (position == 2) {
					tvMsg2.setText("\nSelect column type for Slice & Dice operation.");
					lstColumn.add("Item Name");
					lstColumn.add("Month");
					lstColumn.add("Quarter");
					lstColumn.add("Year");
					lstColumn.add("Town");
					lstColumn.add("City");
					lstColumn.add("Country");

					llOlap.addView(tvMsg2);
					llOlap.addView(spColumn);

					// Show child for the column that user selected 
					spColumn.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							lstColumnChild.clear();
							llOlap.removeView(tvMsg3);
							llOlap.removeView(spColumnChild);

							mySQLiteAdapter.openToRead();
							if (position == 0) {
								tvMsg3.setText("\nChoose a field: ");
								List<String> field = mySQLiteAdapter.getSliceField("Item Name");

								for (int i = 0; i < field.size(); i++)
									lstColumnChild.add(field.get(i));

							} else if (position == 1) {
								tvMsg3.setText("\nChoose a field: ");
								List<String> field = mySQLiteAdapter.getSliceField("Month");

								for (int i = 0; i < field.size(); i++) {
									if (field.get(i).equals("1")) {
										field.remove(i);
										field.add(i, "January");
									} else if (field.get(i).equals("2")) {
										field.remove(i);
										field.add(i, "February");
									} else if (field.get(i).equals("3")) {
										field.remove(i);
										field.add(i, "March");
									} else if (field.get(i).equals("4")) {
										field.remove(i);
										field.add(i, "April");
									} else if (field.get(i).equals("5")) {
										field.remove(i);
										field.add(i, "May");
									} else if (field.get(i).equals("6")) {
										field.remove(i);
										field.add(i, "June");
									} else if (field.get(i).equals("7")) {
										field.remove(i);
										field.add(i, "July");
									} else if (field.get(i).equals("8")) {
										field.remove(i);
										field.add(i, "August");
									} else if (field.get(i).equals("9")) {
										field.remove(i);
										field.add(i, "September");
									} else if (field.get(i).equals("10")) {
										field.remove(i);
										field.add(i, "October");
									} else if (field.get(i).equals("11")) {
										field.remove(i);
										field.add(i, "November");
									} else if (field.get(i).equals("12")) {
										field.remove(i);
										field.add(i, "December");
									}

									lstColumnChild.add(field.get(i));
								}
							} else if (position == 2) {
								tvMsg3.setText("\nChoose a field: ");
								List<String> field = mySQLiteAdapter.getSliceField("Quarter");

								for (int i = 0; i < field.size(); i++) 									
									lstColumnChild.add(field.get(i));
							} else if (position == 3) {
								tvMsg3.setText("\nChoose a field: ");
								List<String> field = mySQLiteAdapter.getSliceField("Year");

								for (int i = 0; i < field.size(); i++) 									
									lstColumnChild.add(field.get(i));
							} else if (position == 4) {
								tvMsg3.setText("\nChoose a field: ");
								List<String> field = mySQLiteAdapter.getSliceField("Town");

								for (int i = 0; i < field.size(); i++) 									
									lstColumnChild.add(field.get(i));
							} else if (position == 5) {
								tvMsg3.setText("\nChoose a field: ");
								List<String> field = mySQLiteAdapter.getSliceField("City");

								for (int i = 0; i < field.size(); i++) 									
									lstColumnChild.add(field.get(i));
							} else if (position == 6) {
								tvMsg3.setText("\nChoose a field: ");
								List<String> field = mySQLiteAdapter.getSliceField("Country");

								for (int i = 0; i < field.size(); i++) 									
									lstColumnChild.add(field.get(i));
							}
							mySQLiteAdapter.close();

							llOlap.addView(tvMsg3);
							llOlap.addView(spColumnChild);

							spColumnChild.setAdapter(adapterColumnChild);
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub

						}});
				} /*else if (position == 3) {
					tvMsg2.setText("\nSelect column for Pivot (rotate) operation.");

					llOlap.addView(tvMsg2);
				}*/

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
				String selectedColumn = null, selectedColumnChild = null;

				if (!(selectedOlapOperation.equals(olapOperation[0].toString())))
					selectedColumn = spColumn.getSelectedItem().toString();

				// Select ..., no operation is chosen
				if (selectedOlapOperation.equals(olapOperation[0].toString())) {
					Toast.makeText(getApplicationContext(),
							"You must choose an Olap Operation.", Toast.LENGTH_SHORT).show();
				} 

				// Drill Up and Down Operation
				else if (selectedOlapOperation.equals(olapOperation[1].toString())) {					
					Intent i = new Intent(MainActivity.this, RollUp.class);	
					i.putExtra("column", selectedColumn);
					startActivity(i);
					finish();
				} 

				// Slice and Dice Operation
				else if (selectedOlapOperation.equals(olapOperation[2].toString())) {
					selectedColumnChild = spColumnChild.getSelectedItem().toString();

					Intent i = new Intent(MainActivity.this, Slice.class);	
					i.putExtra("column", selectedColumn);
					i.putExtra("columnChild", selectedColumnChild);
					startActivity(i);
					finish();
				} 

				/*else if (selectedOlapOperation.equals(olapOperation[3].toString())) {
					Intent i = new Intent(MainActivity.this, Slice.class);	
					i.putExtra("column", selectedColumn);
					startActivity(i);
					finish();
				}*/
			}
		}).setNeutralButton("Cancel", null).show();
	}
}
