/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

public class AppHatate extends Application {
	
	private static RequestQueue _queue;
	
	@Override
	public void onCreate() {
		initQueue();
	}
	
	private static void initQueue() {
		if(_queue == null) _queue = Volley.newRequestQueue(getApplicationContext());
	}
	
	public static RequestQueue getRequestQueue() {
		initQueue();
		return _queue;
	}
}