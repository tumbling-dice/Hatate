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
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.content.Context;
import android.content.Intent;

/**
 * アプリのアップデート後にアラームを再セットするBroadcastReceiver.
 */
@ExtensionMethod({PrefGetter.class})
public class UpdateReceiver extends AsyncBroadcastReceiver {

	@Override
	protected void asyncOnReceive(Context context, Intent intent) {
		if(intent == null || !context.isNoisy()) return;

		//Log.d("UpdateReceiver", "onReceive");

		val action = intent.getAction();
		val packagePath = intent.getDataString();

		if(packagePath == null) return;

		if(Intent.ACTION_PACKAGE_REPLACED.equals(action)
				&& packagePath.equals("package:" + context.getPackageName())) {
			//Log.d("UpdateReceiver", "this intent is PACKAGE_REPLACED");
			Util.setAlarm(context);
		}
	}

	@Override
	protected void onUIThread(Context context, Intent intent) {
		// not implement
	}

}
