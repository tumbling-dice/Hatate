/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.volley.yo;

/**
 * Yo APIs Request.
 * @see YoAPI
 * @see YoParam
 * @see YoAccount
 */
public class YoRequest extends StringRequest {

	private YoAPI _api;
	private YoParam _param;
	private YoAccount _accounts;
	private String _apiToken;

	/**
	 * yo.
	 * @param param POSTで渡すデータ
	 * @param listener 成功時処理
	 * @param errorListener エラー時処理
	 * @return
	 * @see YoParam
	 * @see YoAPI#Yo
	 */
	public static YoRequest yo(YoParam param
		, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		return new YoRequest(param, YoAPI.Yo, listener, errorListener);
	}

	/**
	 * yo all.
	 * @param param POSTで渡すデータ
	 * @param listener 成功時処理
	 * @param errorListener エラー時処理
	 * @return
	 * @see YoParam
	 * @see YoAPI#YoAll
	 */
	public static YoRequest yoAll(YoParam param
		, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		return new YoRequest(param, YoAPI.YoAll, listener, errorListener);
	}

	/**
	 * accounts.
	 * @param accounts POSTで渡すデータ
	 * @param listener 成功時処理
	 * @param errorListener エラー時処理
	 * @return
	 * @see YoAccount
	 * @see YoAPI#Accounts
	 */
	public static YoRequest accounts(YoAccount accounts
		, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		return new YoRequest(accounts, listener, errorListener);
	}

	/**
	 * subscribersCount.
	 * @param apiToken api_token
	 * @param listener 成功時処理
	 * @param errorListener エラー時処理
	 * @see YoAPI#SubscribersCount
	 */
	public static YoRequest subscribersCount(String apiToken
		, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		return new YoRequest(apiToken, listener, errorListener);
	}

	/* yo | yoall */
	private YoRequest(YoParam param, YoAPI api
		, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		super(Request.Method.POST, api.getValue(), listener, errorListener);
		_param = param;
		_api = api;
	}

	/* accounts */
	private YoRequest(YoAccount accounts
		, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		super(Request.Method.POST, YoAPI.Accounts.getValue(), listener, errorListener);
		_accounts = accounts;
		_api = YoAPI.Accounts;
	}

	/* subscribers_count */
	private YoRequest(String apiToken
		, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		// Note: subscribers_countだけは現状GETメソッドとなっている
		super(Request.Method.GET, YoAPI.SubscribersCount.getValue() + String.format("?api_token=%s", apiToken)
			, listener, errorListener);
		_apiToken = apiToken;
		_api = YoAPI.SubscribersCount;
	}

	@Override
	public Map<String, String> getHeaders() {
		// FIXME: いるのかな…？
		val header = new HashMap<String, String>();
		header.put("User-Agent", "Mozilla/5.0");
		header.put("Content-type", "application/x-www-form-urlencoded");
		return header;
	}

	@Override
	protected Map<String, String> getParams() {
		// GETメソッドの場合は特に何もしない
		if(getMethod() == Request.Method.GET) {
			return super.getParams();
		}

		val param = new HashMap<String, String>();

		switch(_api) {
		// yo
		case Yo:
			param.put("username", _param.getUserName());
			param.put("api_token", _param.getApiToken());

			// Note: linkとlocationは同時に渡してはいけないらしい。
			if(_param.getLink() != null && _param.hasLocation()) {
				throw IllegalArgumentException("'yo' api can only send link OR location but not both.");
			} else if(_param.getLink() != null) {
				param.put("link", _param.getLink())
			} else if(_param.hasLocation()) {
				param.put("location", String.format("%d,%d", _param.getLatitude(), _param.getLongitude()));
			}
			break;
		// yoall
		case YoAll:
			param.put("api_token", _param.getApiToken());
			if(_param.getLink() != null) {
				param.put("link", _param.getLink())
			}
			break;
		// accounts
		case Accounts:
			param.put("new_account_username", _accounts.getNewAccountUserName());
			param.put("new_account_passcode", _accounts.getNewAccountPasscode());
			param.put("api_token", _accounts.getApiToken());

			if(_accounts.getCallbackUrl() != null) {
				param.put("callback_url", _accounts.getCallbackUrl());
			}

			if(_accounts.getEmall() != null) {
				param.put("email", _accounts.getEmail());
			}

			if(_accounts.getDescription() != null) {
				param.put("description", _accounts.getDescription());
			}

			if(_accounts.isNeedsLocation()) {
				param.put("needs_location", "true");
			}

			break;
		}

		return param;
	}
}
