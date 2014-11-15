/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.volley.yo;

import lombok.Getter;

/**
 * Yoで使用できるAPIのWrapper.
 * @see YoRequest
 */
public enum YoAPI {
	/** Yo (http://api.justyo.co/yo/) */
	Yo("http://api.justyo.co/yo/"),
	/** YoAll (http://api.justyo.co/yoall/) */
	YoAll("http://api.justyo.co/yoall/"),
	/** Accounts (https://api.justyo.co/accounts/) */
	Accounts("https://api.justyo.co/accounts/"),
	/** SubscribersCount (https://api.justyo.co/subscribers_count/) */
	SubscribersCount("https://api.justyo.co/subscribers_count/");

	@Getter private final String value;

	private YoAPI(String value) {
		this.value = value;
	}
}
