/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.volley.weather;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.Data;
import android.annotation.SuppressLint;

@Data
public class WeatherResponse implements Serializable {

	private static final long serialVersionUID = -8449181199444648788L;

	private String name;
	private List<WeatherData> datas;

	@Data
	public static class WeatherData {

		WeatherData(){}

		private Date date;
		private double minTemp;
		private double maxTemp;
		private String weather;
		private String weatherIconId;

		public String getWeatherUrl() {
			return "http://openweathermap.org/img/w/" + weatherIconId;
		}

		@SuppressLint("SimpleDateFormat")
		private static final SimpleDateFormat _sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE);

		public String getDateToStirng() {
			return date != null ? _sdf.format(date) : null;
		}
	}


}
