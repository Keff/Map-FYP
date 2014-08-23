package com.mad.fyp.tescoolap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Slice extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		int[] mth = {5, 3, 2, 11, 1}; // 3, 5, 2 - failed here
		int temp = 0, temp2 = 0, counter = 0;

		for (int j = 0; j < mth.length; j++) {
			counter = 0;
			temp = mth[j]; // temp 3

			if (j > 0) {
				while (j-counter != 0) {
					counter++;
					temp2 = mth[j-counter]; // temp2 = 5

					if (counter == 1) {
						if (temp < temp2) { // 3 < 5
							mth[j-counter] = temp; // 3
							mth[j] = temp2; // 5
						}
					} else {
						if (temp < temp2) { // 3 < 5
							mth[j-counter] = temp; // 3
							mth[j-counter+1] = temp2; // 5
						}
					}
				}
			}

		}

		TextView tv = new TextView(this);
		
		for (int i = 0; i < mth.length; i++)
			tv.append(String.valueOf(mth[i]) + "\n");
		
		ll.addView(tv);
		setContentView(ll);
	}
}
