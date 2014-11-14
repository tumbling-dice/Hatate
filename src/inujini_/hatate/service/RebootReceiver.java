/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.service;

import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.util.Util;
import lombok.experimental.ExtensionMethod;
import android.content.Context;
import android.content.Intent;

/**
 * 端末再起動時にアラームを再セットするBroadcastReceiver.
 */
@ExtensionMethod({PrefGetter.class})
public class RebootReceiver extends AsyncBroadcastReceiver {

	@Override
	protected void asyncOnReceive(Context context, Intent intent) {
		if(intent == null || !context.isNoisy()) return;

		//Log.d("RebootReceiver", "onReceive");

		if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			//Log.d("RebootReceiver", "this intent is BOOT_COMPLETED");
			Util.setAlarm(context);
		}

	}

}
