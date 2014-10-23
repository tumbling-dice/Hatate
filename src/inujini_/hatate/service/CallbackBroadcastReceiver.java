/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.service;

import java.io.Serializable;

import twitter4j.auth.AccessToken;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Oauth認証後のコールバック.
 */
public abstract class CallbackBroadcastReceiver extends BroadcastReceiver {

	private static final String ACTION_CALLBACK = "twitter4j.auth.action.callback";
	private static final String KEY_DATA = "data";

	/* package private */ static class Data implements Serializable {
		private static final long serialVersionUID = -2779961412302689699L;
		AccessToken token;
		Exception exception;
		boolean isSuccess;

		static Data create(AccessToken token) {
			Data data = new Data();
			data.token = token;
			data.isSuccess = true;
			return data;
		}

		static Data create(Exception exception) {
			Data data = new Data();
			data.exception = exception;
			data.isSuccess = false;
			return data;
		}
	}

	/**
	 * CallbackBroadcastReceiverへのIntent作成
	 * @param data
	 * @return
	 */
	public static Intent createIntent(Data data) {
		Intent i = new Intent();
		i.setAction(ACTION_CALLBACK);
		i.putExtra(KEY_DATA, data);
		return i;
	}

	/**
	 * CallbackBroadcastReceiverを登録する際のIntentFilter作成
	 * @return
	 */
	public static IntentFilter createIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_CALLBACK);
		return filter;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Data data = (Data) intent.getSerializableExtra(KEY_DATA);

		try {
			if(data.isSuccess) {
				onSuccess(data.token);
			} else {
				onError(data.exception);
			}
		} finally {
			context.unregisterReceiver(this);
		}
	}

	/**
	 * Oauth認証成功後処理.
	 * @param token
	 */
	public abstract void onSuccess(AccessToken token);
	
	/**
	 * Oauth認証において何らかの例外が発生した場合のエラーハンドラ.
	 * （シリアライズ / デシリアライズにおける{@link IOException}を含む）
	 * @param exception
	 */
	public abstract void onError(Exception exception);
}