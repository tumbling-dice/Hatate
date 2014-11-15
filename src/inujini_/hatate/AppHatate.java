/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 *
 */
public class AppHatate extends Application {

	private static RequestQueue _queue;

	@Override
	public void onCreate() {
		initQueue(getApplicationContext());
	}

	private static void initQueue(Context context) {
		if(_queue == null) _queue = Volley.newRequestQueue(context);
	}

	/**
	 * {@link RequestQueue}の取得.
	 * @return {@link RequestQueue}
	 */
	public static RequestQueue getRequestQueue(Context context) {
		initQueue(context);
		return _queue;
	}
}
