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

import lombok.Data;

@Data
public class WeatherResponse implements Serializable {

	private static final long serialVersionUID = -8449181199444648788L;

	private String name;
	private double minTemp;
	private double maxTemp;
	private String weather;
	private String weatherIconId;

	/*public double getMinTemp() {
		return minTemp - 273.15;
	}

	public double getMaxTemp() {
		return maxTemp - 273.15;
	}*/

	public String getWeatherUrl() {
		return "http://openweathermap.org/img/w/" + weatherIconId;
	}
}
