/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.volley.yo;

import inujini_.hatate.volley.WebAPI;

import com.android.volley.Request.Method;

import lombok.Getter;

/**
 * Yoで使用できるAPIのWrapper.
 * @see YoRequest
 */
public enum YoAPI implements WebAPI {
	/** Yo (http://api.justyo.co/yo/) */
	YO("http://api.justyo.co/yo/", Method.POST),
	/** YoAll (http://api.justyo.co/yoall/) */
	YO_ALL("http://api.justyo.co/yoall/", Method.POST),
	/** Accounts (https://api.justyo.co/accounts/) */
	ACCOUNTS("https://api.justyo.co/accounts/", Method.POST),
	/** SubscribersCount (https://api.justyo.co/subscribers_count/) */
	SUBSCRIBERS_COUNT("https://api.justyo.co/subscribers_count/", Method.GET);

	@Getter private final String endPoint;
	@Getter private final int method;

	private YoAPI(String endPoint, int method) {
		this.endPoint = endPoint;
		this.method = method;
	}
}
