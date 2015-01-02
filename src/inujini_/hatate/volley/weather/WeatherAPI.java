/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.volley.weather;

import inujini_.hatate.R;
import inujini_.hatate.volley.WebAPI;
import lombok.Getter;
import android.content.Context;

import com.android.volley.Request.Method;

public enum WeatherAPI implements WebAPI {
	CURRENT("http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&APPID=%s&mode=json&units=metric", Method.GET),
	WEAKLY("http://api.openweathermap.org/data/2.5/forecast/daily?lat=%f&lon=%f&APPID=%s&cnt=7&mode=json&units=metric"
			, Method.GET);

	@Getter private final String endPoint;
	@Getter private final int method;

	private WeatherAPI(String endPoint, int method) {
		this.endPoint = endPoint;
		this.method = method;
	}

	public static String getKey(Context context) {
		return context.getString(R.string.weather_api_key);
	}

	public String getEndPoint(double latitude, double longitude, Context context) {
		return String.format(this.endPoint, latitude, longitude, getKey(context));
	}

}
