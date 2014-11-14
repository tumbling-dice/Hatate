/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.service;

import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 非同期通信でラップしたBroadcastReceiver.
 */
public abstract class AsyncBroadcastReceiver extends BroadcastReceiver {

	@Override
	public final void onReceive(Context context, final Intent intent) {
		ReactiveAsyncTask.create(new Func1<Context, Void>() {
			@Override
			public Void call(Context x) {
				asyncOnReceive(x, intent);
				return null;
			}
		}).execute(context);
	}

	/**
	 * 非同期処理で行う内容.
	 * @param context
	 * @param intent
	 * @see BroadcastReceiver#onReceive(Context, Intent)
	 */
	protected abstract void asyncOnReceive(Context context, Intent intent);

}
