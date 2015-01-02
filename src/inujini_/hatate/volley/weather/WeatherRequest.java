/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.volley.weather;

import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.util.JSONExtensions;
import inujini_.hatate.volley.weather.WeatherResponse.WeatherData;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lombok.val;
import lombok.experimental.ExtensionMethod;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

@ExtensionMethod({JSONExtensions.class})
public class WeatherRequest extends JsonRequest<WeatherResponse> {

	private final WeatherAPI _api;

	public WeatherRequest(WeatherAPI api, double latitude, double longitude
			, Context context, Listener<WeatherResponse> listener, ErrorListener errorListener) {
		super(api.getMethod(), api.getEndPoint(latitude, longitude, context), null, listener, errorListener);
		_api = api;
	}

	@Override
	protected Response<WeatherResponse> parseNetworkResponse(NetworkResponse response) {
		try {
			val jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

			switch (_api) {
			case CURRENT:
				return Response.success(current(new JSONObject(jsonString)),
						HttpHeaderParser.parseCacheHeaders(response));
			case WEAKLY:
				return Response.success(weakly(new JSONObject(jsonString)),
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

		resp.setName(json.getStringOrDefault("name"));
		val data = new WeatherData();
		data.setDate(new Date());

		val main = json.getJSONObject("main");
		data.setMaxTemp(main.getDoubleOrDefault("temp_max"));
		data.setMinTemp(main.getDoubleOrDefault("temp_min"));
		val weather = json.getJSONArray("weather").getJSONObject(0);
		data.setWeather(weather.getStringOrDefault("main"));
		data.setWeatherIconId(weather.getStringOrDefault("icon"));

		val datas = new ArrayList<WeatherData>();
		datas.add(data);
		resp.setDatas(datas);

		return resp;
	}

	private WeatherResponse weakly(JSONObject json) throws JSONException {
		val resp = new WeatherResponse();

		val city = json.getJSONObject("city");
		resp.setName(city.getStringOrDefault("name"));

		val list = json.getJSONArray("list");
		val datas = list.map(new Func1<JSONObject, WeatherData>(){
			@Override
			public WeatherData call(JSONObject x) {
				val data = new WeatherData();
				try {
					val c = Calendar.getInstance();
					c.setTimeInMillis((x.getLongOrDefault("dt") * 1000));
					// GMT + 9
					c.add(Calendar.HOUR_OF_DAY, 9);
					data.setDate(c.getTime());
					val temp = x.getJSONObject("temp");
					data.setMaxTemp(temp.getDoubleOrDefault("max"));
					data.setMinTemp(temp.getDoubleOrDefault("min"));
					val weather = x.getJSONArray("weather").getJSONObject(0);
					data.setWeather(weather.getStringOrDefault("main"));
					data.setWeatherIconId(weather.getStringOrDefault("icon"));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return data;
			}
		});

		resp.setDatas(datas);

		return resp;
	}


}
