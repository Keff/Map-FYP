package com.mad.fyp.tescoolap;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteAdapter {
	public static final String MYDATABASE_NAME = "tesco.db";
	public static final String MYDATABASE_TABLE = "tesco_olap_table";
	public static final int MYDATABASE_VERSION = 2;

	// database column
	public static final String itemName = "name";
	public static final String itemQuantity = "quantity";
	public static final String itemPrice = "price";
	public static final String month_of_sales = "month";
	public static final String quarter_of_sales = "quarter";
	public static final String year_of_sales = "year";
	public static final String sales_town = "town";
	public static final String sales_city = "city";
	public static final String sales_country = "country";

	private static final String SCRIPT_CREATE_DATABASE = "create table "
			+ MYDATABASE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ itemName + " text, " + itemQuantity + " int, "
			+ itemPrice + " text, " + month_of_sales + " text, "
			+ quarter_of_sales + " text, " + year_of_sales + " int, "
			+ sales_town + " text, " + sales_city + " text, "
			+ sales_country + " text );"; 

	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;
	private Context context;

	public SQLiteAdapter(Context c) {
		context = c;
	}

	public SQLiteAdapter openToRead() throws
	android.database.SQLException {

		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, 
				null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();

		return this;
	}

	public SQLiteAdapter openToWrite() throws
	android.database.SQLException {

		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, 
				null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();

		return this;
	}

	public void close() {
		sqLiteHelper.close();
	}

	public long insert(String name, int quantity, String price, String month, 
			String quarter, int year, String town, String city, String country) {
		// TODO Auto-generated method stub
		ContentValues contentValues = new ContentValues();
		contentValues.put(itemName, name);
		contentValues.put(itemQuantity, quantity);
		contentValues.put(itemPrice, price);
		contentValues.put(month_of_sales, month);
		contentValues.put(quarter_of_sales, quarter);
		contentValues.put(year_of_sales, year);
		contentValues.put(sales_town, town);
		contentValues.put(sales_city, city);
		contentValues.put(sales_country, country);

		return sqLiteDatabase.insert(MYDATABASE_TABLE, 
				null, contentValues);
	}

	public int deleteAll() {
		sqLiteDatabase.execSQL("delete from sqlite_sequence where name='tesco_olap_table';");
		return sqLiteDatabase.delete(MYDATABASE_TABLE, null, null);
	}

	// return the number of items in database
	public int count() {
		// TODO Auto-generated method stub
		String[] columns = new String[] {itemName};

		/* .query(String table, String[] columns, String selection, String[] selectionArrays, 
		 * Group By, String having, String order);
		 * 
		 */
		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, 
				columns, null, null, null, null, null);

		int count = 0;

		cursor.moveToFirst();
		count = cursor.getCount();
		cursor.close();

		return count;
	}

	/* Part 1 - Drill up/down operation (combining drill up and down)
	 * Retrieve the column that rolled up (in highest hierarchy)
	 */
	public List<String> getRollUp_Column(String col) {
		String[] columns = null;
		String sortOrder = null;
		int index_CONTENT = 0;

		if (col.equals("Month & Quarter")) {
			columns = new String[] {quarter_of_sales};
			sortOrder = "quarter ASC";
		} else if (col.equals("Town & City")) {
			columns = new String[] {sales_city};
			sortOrder = "city ASC";
		} else if (col.equals("City & Country")) {
			columns = new String[] {sales_country};
			sortOrder = "country ASC";
		}

		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, 
				columns, null, null, null, null, sortOrder);

		if (col.equals("Month & Quarter"))
			index_CONTENT = cursor.getColumnIndex(quarter_of_sales);
		else if (col.equals("Town & City"))
			index_CONTENT = cursor.getColumnIndex(sales_city);
		else if (col.equals("City & Country"))
			index_CONTENT = cursor.getColumnIndex(sales_country);

		int cursor_no = 0;
		List<String> lst = new ArrayList<String>();
		cursor.moveToFirst();

		while (!(cursor.isAfterLast())) 
		{
			if (cursor_no == 0) {
				lst.add(cursor.getString(index_CONTENT));
				cursor_no++;
			} else if (cursor_no > 0 && !(lst.get(cursor_no-1).equals(cursor.getString(index_CONTENT)))) {
				lst.add(cursor.getString(index_CONTENT));
				cursor_no++;
			}

			cursor.moveToNext();
		}

		if (col.equals("Month & Quarter")) {
			lst = renameQuarter(lst);
		}

		cursor.close();
		return lst;
	}

	/* Part 2 - Drill up/down operation
	 * Retrieve the column under the previous rolled up column
	 */
	public List<String> getRollDown_Column(String col, String ret) {
		String[] columns = null;
		String sortOrder = null;
		int index_CONTENT = 0;
		Cursor cursor = null;

		if (col.equals("Month & Quarter")) {
			columns = new String[] {month_of_sales};
			sortOrder = "month ASC";

			if (ret.equals("Quarter 1"))
				ret = "Q1";
			else if (ret.equals("Quarter 2"))
				ret = "Q2";
			else if (ret.equals("Quarter 3"))
				ret = "Q3";
			else if (ret.equals("Quarter 4"))
				ret = "Q4";

			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, quarter_of_sales + "=?", new String[]{ret}, null, null, sortOrder);
		} else if (col.equals("Town & City")) {
			columns = new String[] {sales_town};
			sortOrder = "town ASC";

			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, sales_city + "=?", new String[]{ret}, null, null, sortOrder);
		} else if (col.equals("City & Country")) {
			columns = new String[] {sales_city};
			sortOrder = "city ASC";

			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, sales_country + "=?", new String[]{ret}, null, null, sortOrder);
		}

		if (col.equals("Month & Quarter"))			
			index_CONTENT = cursor.getColumnIndex(month_of_sales);
		else if (col.equals("Town & City"))
			index_CONTENT = cursor.getColumnIndex(sales_town);
		else if (col.equals("City & Country"))
			index_CONTENT = cursor.getColumnIndex(sales_city);

		int cursor_no = 0;
		List<String> lst = new ArrayList<String>();
		cursor.moveToFirst();

		while (!(cursor.isAfterLast())) {
			if (cursor_no == 0) {				
				lst.add(cursor.getString(index_CONTENT));
				cursor_no++;
			} else if (cursor_no > 0 && !(lst.get(cursor_no-1).equals(cursor.getString(index_CONTENT)))) {
				lst.add(cursor.getString(index_CONTENT));
				cursor_no++;
			}

			cursor.moveToNext();
		}

		// To place month in correct order, from January to December
		if (col.equals("Month & Quarter")) {
			// Sort month order by calling function		
			lst = sortMonth(lst);
		}

		cursor.close();
		return lst;
	}

	/*	Part 3 - Drill up/down operation
	 *	retrieve the whole rows for the selected roll down column
	 */
	public List<String> getRollDown_Data(String col, String ret) {
		String[] columns = new String[] {itemName, itemQuantity, itemPrice, month_of_sales,
				quarter_of_sales, year_of_sales, sales_town, sales_city, sales_country};
		String sortOrder = null;
		Cursor cursor = null;

		if (col.equals("Month & Quarter")) {
			sortOrder = "month ASC";

			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, month_of_sales + "=?", new String[]{ret}, null, null, sortOrder);
		} else if (col.equals("Town & City")) {
			sortOrder = "town ASC";

			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, sales_town + "=?", new String[]{ret}, null, null, sortOrder);
		} else if (col.equals("City & Country")) {
			sortOrder = "city ASC";

			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, sales_city + "=?", new String[]{ret}, null, null, sortOrder);
		}

		int itemname = cursor.getColumnIndex(itemName);
		int quantity = cursor.getColumnIndex(itemQuantity);
		int price = cursor.getColumnIndex(itemPrice);
		int month =  cursor.getColumnIndex(month_of_sales);
		int quarter =  cursor.getColumnIndex(quarter_of_sales);
		int year =  cursor.getColumnIndex(year_of_sales);
		int town = cursor.getColumnIndex(sales_town);
		int city = cursor.getColumnIndex(sales_city);
		int country = cursor.getColumnIndex(sales_country);

		List<String> result = new ArrayList<String>();

		// now always return the whole result, no selected column anymore
		for (cursor.moveToFirst(); !(cursor.isAfterLast());
				cursor.moveToNext()) {
			result.add(cursor.getString(itemname) + ", " + Integer.parseInt(cursor.getString(quantity)) + ", "
					+ cursor.getString(price) + ", " + cursor.getString(month) + ", "
					+ cursor.getString(quarter) + ", " + cursor.getString(year) + ", " + cursor.getString(town) + ", "
					+ cursor.getString(city) + ", " + cursor.getString(country) + '\n');
		}

		cursor.close();
		return result;
	}

	/* Slice and Dice - To return single data/field that belong to that column */
	public List<String> getSliceField(String col) {
		String[] columns = null;
		String sortOrder = null;
		int index_CONTENT = 0;

		if (col.equals("Item Name")) {
			columns = new String[] {itemName};
			sortOrder = "name ASC";
		} else if (col.equals("Month")) {
			columns = new String[] {month_of_sales};
			sortOrder = "month ASC";
		} else if (col.equals("Quarter")) {
			columns = new String[] {quarter_of_sales};
			sortOrder = "quarter ASC";
		} else if (col.equals("Year")) {
			columns = new String[] {year_of_sales};
			sortOrder = "year ASC";
		} else if (col.equals("Town")) {
			columns = new String[] {sales_town};
			sortOrder = "town ASC";
		} else if (col.equals("City")) {
			columns = new String[] {sales_city};
			sortOrder = "city ASC";
		} else if (col.equals("Country")) {
			columns = new String[] {sales_country};
			sortOrder = "country ASC";
		}

		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, 
				columns, null, null, null, null, sortOrder);

		if (col.equals("Item Name")) {
			index_CONTENT = cursor.getColumnIndex(itemName);
		} else if (col.equals("Month")) {
			index_CONTENT = cursor.getColumnIndex(month_of_sales);
		} else if (col.equals("Quarter")) {
			index_CONTENT = cursor.getColumnIndex(quarter_of_sales);
		} else if (col.equals("Year")) {
			index_CONTENT = cursor.getColumnIndex(year_of_sales);
		} else if (col.equals("Town")) {
			index_CONTENT = cursor.getColumnIndex(sales_town);
		} else if (col.equals("City")) {
			index_CONTENT = cursor.getColumnIndex(sales_city);
		} else if (col.equals("Country")) {
			index_CONTENT = cursor.getColumnIndex(sales_country);
		}

		int cursor_no = 0;
		List<String> lst = new ArrayList<String>();
		cursor.moveToFirst();

		while (!(cursor.isAfterLast())) 
		{
			if (cursor_no == 0) {
				lst.add(cursor.getString(index_CONTENT));
				cursor_no++;
			} else if (cursor_no > 0 && !(lst.get(cursor_no-1).equals(cursor.getString(index_CONTENT)))) {
				lst.add(cursor.getString(index_CONTENT));
				cursor_no++;
			}

			cursor.moveToNext();
		}

		if (col.equals("Month")) {
			// Sort month order by calling function		
			lst = sortMonth(lst);
		} else if (col.equals("Quarter")) {
			// Rename quarter by calling function
			lst = renameQuarter(lst);
		}

		cursor.close();
		return lst;
	}

	/* Slice and Dice - Retrieve the data if the data is needed (based on some circumstances of course) */
	public List<Object> getSliceData(String col, String field) {
		String[] columns = new String[] {itemName, itemQuantity, itemPrice, month_of_sales,
				quarter_of_sales, year_of_sales, sales_town, sales_city, sales_country};
		Cursor cursor = null;

		if (col.equals("Item Name")) {		
			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, itemName + "=?", new String[]{field}, null, null, null);
		} else if (col.equals("Month")) {
			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, month_of_sales + "=?", new String[]{field}, null, null, null);
		} else if (col.equals("Quarter")) {
			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, quarter_of_sales + "=?", new String[]{field}, null, null, null);
		} else if (col.equals("Year")) {
			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, year_of_sales + "=?", new String[]{field}, null, null, null);
		} else if (col.equals("Town")) {
			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, sales_town + "=?", new String[]{field}, null, null, null);
		} else if (col.equals("City")) {
			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, sales_city + "=?", new String[]{field}, null, null, null);
		} else if (col.equals("Country")) {
			cursor = sqLiteDatabase.query(MYDATABASE_TABLE,
					columns, sales_country + "=?", new String[]{field}, null, null, null);
		}

		int itemname = cursor.getColumnIndex(itemName);
		int quantity = cursor.getColumnIndex(itemQuantity);
		int price = cursor.getColumnIndex(itemPrice);
		int month =  cursor.getColumnIndex(month_of_sales);
		int quarter =  cursor.getColumnIndex(quarter_of_sales);
		int year =  cursor.getColumnIndex(year_of_sales);
		int town = cursor.getColumnIndex(sales_town);
		int city = cursor.getColumnIndex(sales_city);
		int country = cursor.getColumnIndex(sales_country);

		List<String> result = new ArrayList<String>();
		List<Object> childItems = new ArrayList<Object>();

		// now always return the whole result, no selected column anymore
		for (cursor.moveToFirst(); !(cursor.isAfterLast());
				cursor.moveToNext()) {
			result.add(cursor.getString(itemname) + ", " + Integer.parseInt(cursor.getString(quantity)) + ", "
					+ cursor.getString(price) + ", " + cursor.getString(month) + ", "
					+ cursor.getString(quarter) + ", " + cursor.getString(year) + ", " + cursor.getString(town) + ", "
					+ cursor.getString(city) + ", " + cursor.getString(country) + '\n');
		}

		childItems.add(result);

		cursor.close();
		return childItems;

	}

	// Rename to more understanding word to represent quarter
	private List<String> renameQuarter(List<String> lst) {
		for (int i = 0; i < lst.size(); i++) {
			if (lst.get(i).equals("Q1")) {
				lst.remove(i);
				lst.add(i, "Quarter 1");
			} else if (lst.get(i).equals("Q2")) {
				lst.remove(i);
				lst.add(i, "Quarter 2");
			} else if (lst.get(i).equals("Q3")) {
				lst.remove(i);
				lst.add(i, "Quarter 3");
			} else if (lst.get(i).equals("Q4")) {
				lst.remove(i);
				lst.add(i, "Quarter 4");
			}
		}

		return lst;
	}

	// To sort month in correct order, from January to December
	private List<String> sortMonth(List<String> lst) {	
		int[] mth = new int[lst.size()];

		for (int i = 0; i < lst.size(); i++) {
			if (lst.get(i).equals("January"))
				mth[i] = 1;
			else if (lst.get(i).equals("February"))
				mth[i] = 2;
			else if (lst.get(i).equals("March"))
				mth[i] = 3;
			else if (lst.get(i).equals("April"))
				mth[i] = 4;
			else if (lst.get(i).equals("May"))
				mth[i] = 5;
			else if (lst.get(i).equals("June"))
				mth[i] = 6;
			else if (lst.get(i).equals("July"))
				mth[i] = 7;
			else if (lst.get(i).equals("August"))
				mth[i] = 8;
			else if (lst.get(i).equals("September"))
				mth[i] = 9;
			else if (lst.get(i).equals("October"))
				mth[i] = 10;
			else if (lst.get(i).equals("November"))
				mth[i] = 11;
			else if (lst.get(i).equals("December"))
				mth[i] = 12;
		}

		int temp = 0, temp2 = 0, counter = 0;

		for (int j = 0; j < mth.length; j++) {
			counter = 0;
			temp = mth[j];

			if (j > 0) {
				while (j-counter != 0) {
					counter++;
					temp2 = mth[j-counter];

					if (counter == 1) {
						if (temp < temp2) {
							mth[j-counter] = temp;
							mth[j] = temp2;
						}
					} else {
						if (temp < temp2) {
							mth[j-counter] = temp;
							mth[j-counter+1] = temp2;
						}
					}
				}
			}
		}

		lst.clear();
		for (int k = 0; k < mth.length; k++) {
			lst.add(String.valueOf(mth[k]));
		}

		return lst;
	}

	public class SQLiteHelper extends SQLiteOpenHelper {

		public SQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(SCRIPT_CREATE_DATABASE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL(SCRIPT_CREATE_DATABASE);
		}
	}
}
