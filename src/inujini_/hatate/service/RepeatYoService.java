/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.service;

/**
 * Yo.
 */
@ExtensionMethod({PrefGetter.class})
public class RepeatYoService extends IntentService {

	public static final PREF_YO = "prefYo";
	public static final IS_SENT_YO = "isSentYo";

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

		AppHatate.getRequestQueue()
			.add(YoRequest.yo(param)
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
									, error.networkResponse.statuscode
									, error.getMessage()));
						}
					}
				});
	}

	/**
	 * 予約しておいたYoが成功しているかをチェック.
	 * @param context
	 * @return 成功していたらtrue
	 */
	public static boolean isSentYo(Context context) {
		return context.getSharedPreference(PREF_YO, 0).getBoolean(IS_SENT_YO, true)
	}
}
