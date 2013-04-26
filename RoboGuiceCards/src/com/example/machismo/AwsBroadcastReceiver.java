/**
 * 
 */
package com.example.machismo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author e300md
 *
 */
public class AwsBroadcastReceiver extends BroadcastReceiver {

	public static final String ACTION_RESP = "com.example.machismo.MESSAGE_PROCESSED";
	
	private MainActivity mainActivity;
	
	public AwsBroadcastReceiver(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}
	
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context arg0, Intent arg1) {

		this.mainActivity.displayCardBacksInTabView();

	}

}
