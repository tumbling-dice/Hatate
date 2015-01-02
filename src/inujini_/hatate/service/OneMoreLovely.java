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
import android.content.Context;
import android.content.Intent;

/**
 * スヌーズをセットするBroadcastReceiver.
 */
public class OneMoreLovely extends PierceReceiver {

	@Override
	public void onAfterPierced(Context context, Intent intent) {
		Util.setSnooze(context);
	}

	@Override
	protected void onUIThread(Context context, Intent intent) {
		// not implement
	}

}
