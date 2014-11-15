/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.service;

import inujini_.hatate.AppHatate;
import inujini_.hatate.R;
import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.util.Util;
import inujini_.hatate.volley.yo.YoParam;
import inujini_.hatate.volley.yo.YoRequest;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Yo.
 */
@ExtensionMethod({PrefGetter.class})
public class RepeatYoService extends IntentService {

	public static final String PREF_YO = "prefYo";
	public static final String IS_SENT_YO = "isSentYo";

	/**
	 * Yo.
	 */
	public RepeatYoService() {
		super("RepeatYoService");
	}

	/**
	 * Yo.
	 * @param name
	 */
	public RepeatYoService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		val context = getApplicationContext();
		val userName = context.getYo();

		if(userName == null || "".equals(userName)) {
			Util.removeRepeatYo(context);
			return;
		}

		val param = new YoParam.Builder(context.getResources().getString(R.string.yo_api_key))
						.userName(userName)
						.build();

		AppHatate.getRequestQueue(getApplicationContext())
			.add(YoRequest.yo(param
				, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d("RepeatYoService", "success yo.");
						Util.removeRepeatYo(context);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error.networkResponse == null) {
							Log.d("RepeatYoService"
								, String.format("error yo. message:%s", error.getMessage()));
						} else {
							Log.d("RepeatYoService"
								, String.format("error yo. statuscode:%d message:%s"
									, error.networkResponse.statusCode
									, error.getMessage()));
						}

						Util.removeRepeatYo(context);
					}
				}));
	}

	/**
	 * 予約しておいたYoが成功しているかをチェック.
	 * @param context
	 * @return 成功していたらtrue
	 */
	public static boolean isSentYo(Context context) {
		return context.getSharedPreferences(PREF_YO, 0).getBoolean(IS_SENT_YO, true);
	}
}
