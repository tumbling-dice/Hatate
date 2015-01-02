/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.volley.weather;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import inujini_.hatate.AppHatate;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 *
 */
public class WeatherManager implements LocationListener {

	private static final String TAG = "WeatherManager";

	private Context _context;
	private LocationManager _locationManager;
	@Accessors(prefix="_") @Getter @Setter private String _provider;
	private static final BlockingQueue<WeatherQueue> _queues = new LinkedBlockingQueue<WeatherQueue>();

	private static final class WeatherQueue {
		public final WeatherAPI api;
		public final Listener<WeatherResponse> handler;
		public final ErrorListener errorHandler;

		public WeatherQueue(WeatherAPI api, Listener<WeatherResponse> handler, ErrorListener errorHandler) {
			this.api = api;
			this.handler = handler;
			this.errorHandler = errorHandler;
		}
	}

	public WeatherManager(Context context) {
		_context = context;
		_locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		val criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		_provider = _locationManager.getBestProvider(criteria, true);
	}

	public void getCurrent(Listener<WeatherResponse> handler, ErrorListener errorHandler) {
		get(WeatherAPI.CURRENT, handler, errorHandler);
	}

	public void getWeakly(Listener<WeatherResponse> handler, ErrorListener errorHandler) {
		get(WeatherAPI.WEAKLY, handler, errorHandler);
	}

	private void get(WeatherAPI api, Listener<WeatherResponse> handler,
			ErrorListener errorHandler) {
		_queues.add(new WeatherQueue(api, handler, errorHandler));
		_locationManager.requestLocationUpdates(_provider, 0, 0, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		if(_context == null) return;

		if(_locationManager != null) {
			_locationManager.removeUpdates(this);
		}

		val queue = _queues.poll();

		if(queue == null) {
			Log.w(TAG, "call onLocationChanged, but queue is empty.");
			return;
		}

		val latitude = location.getLatitude();
		val longitude = location.getLongitude();

		AppHatate.getRequestQueue(_context)
			.add(new WeatherRequest(queue.api, latitude, longitude, _context, queue.handler, queue.errorHandler));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// not implement
	}

	@Override
	public void onProviderEnabled(String provider) {
		// not implement
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// not implement
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			_locationManager = null;
			_context = null;
			_queues.clear();
		} finally {
			super.finalize();
		}
	}

}
