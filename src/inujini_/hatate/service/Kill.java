/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.service;

import inujini_.hatate.util.Util;
import lombok.val;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

public class Kill extends IntentService {

	public static final String ACTION_KILL = "inujini_.hatate.service.action.KILL";

	public Kill() {
		super("Kill");
	}

	public Kill(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Util.removeSnooze(getApplicationContext());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		val notifyManager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notifyManager.cancel(0);
	}
}
