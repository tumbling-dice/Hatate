/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.volley.weather;

import java.io.UnsupportedEncodingException;

import lombok.val;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

public class WeatherRequest extends JsonRequest<WeatherResponse> {

	private final WeatherAPI _api;

	public WeatherRequest(WeatherAPI api, double latitude, double longitude
			, String apiKey, Listener<WeatherResponse> listener, ErrorListener errorListener) {
		super(api.getMethod(), String.format("%s?lat=%f&lon=%f&APPID=%s&units=metric"
					, api.getEndPoint(), latitude, longitude, apiKey)
				, null, listener, errorListener);
		_api = api;
	}

	@Override
	protected Response<WeatherResponse> parseNetworkResponse(NetworkResponse response) {
		try {
			val jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

			switch (_api) {
			case Current:
				return Response.success(current(new JSONObject(jsonString)),
						HttpHeaderParser.parseCacheHeaders(response));
			default:
				return Response.error(new VolleyError("not difined api"));
			}


		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	private WeatherResponse current(JSONObject json) throws JSONException {
		val resp = new WeatherResponse();

		resp.setName(json.getString("name"));

		val main = json.getJSONObject("main");
		resp.setMaxTemp(main.getDouble("temp_max"));
		resp.setMinTemp(main.getDouble("temp_min"));
		val weather = json.getJSONArray("weather").getJSONObject(0);
		resp.setWeather(weather.getString("main"));
		resp.setWeatherIconId(weather.getString("icon"));

		return resp;
	}


}
