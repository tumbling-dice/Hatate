/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.adapter;

import inujini_.hatate.AppHatate;
import inujini_.hatate.R;
import inujini_.hatate.volley.BitmapCache;
import inujini_.hatate.volley.weather.WeatherResponse.WeatherData;

import java.util.List;

import lombok.val;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

public class WeatherAdapter extends ArrayAdapter<WeatherData> {

	private final LayoutInflater _inflater;
	private final ImageLoader _imageLoader;
	private static final String WEATHER_ICON_URI = "http://openweathermap.org/img/w/%s.png";


	public WeatherAdapter(Context context, List<WeatherData> datas) {
		super(context, 0, datas);
		_inflater = LayoutInflater.from(context);
		_imageLoader = new ImageLoader(AppHatate.getRequestQueue(context)
				, new BitmapCache());
	}

	static final class WeatherViewHolder {
		final TextView date;
		final TextView weather;
		final TextView maxTemp;
		final TextView minTemp;
		final ImageView icon;

		WeatherViewHolder(View view) {
			date = (TextView) view.findViewById(R.id.txvDate);
			weather = (TextView) view.findViewById(R.id.txvWeather);
			maxTemp = (TextView) view.findViewById(R.id.txvMaxTemp);
			minTemp = (TextView) view.findViewById(R.id.txvMinTemp);
			icon = (ImageView) view.findViewById(R.id.imgIcon);
		}
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		View view = convertView;
		WeatherViewHolder vh = null;

		if(view == null) {
			view = _inflater.inflate(R.layout.adapter_weather, null);
			vh = new WeatherViewHolder(view);
			view.setTag(vh);
		} else {
			vh = (WeatherViewHolder) view.getTag();
		}

		val item = getItem(position);

		vh.date.setText(item.getDateToStirng());
		vh.weather.setText(item.getWeather());
		vh.maxTemp.setText(String.format("最高気温：%.1f℃", item.getMaxTemp()));
		vh.minTemp.setText(String.format("最低気温：%.1f℃", item.getMinTemp()));
		vh.icon.setTag(item.getDateToStirng());
		_imageLoader.get(String.format(WEATHER_ICON_URI, item.getWeatherIconId()), new ImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				parent.findViewWithTag(item.getDateToStirng()).setVisibility(View.GONE);
			}
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null) {
					val v = (ImageView) parent.findViewWithTag(item.getDateToStirng());
					if(v != null) {
						v.setVisibility(View.VISIBLE);
						v.setImageBitmap(response.getBitmap());
					}
				}
			}
		});


		return view;
	}


}
