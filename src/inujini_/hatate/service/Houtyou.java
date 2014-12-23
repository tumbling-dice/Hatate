/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.service;

import inujini_.hatate.AppHatate;
import inujini_.hatate.MainActivity;
import inujini_.hatate.R;
import inujini_.hatate.SpellCardHistoryActivity;
import inujini_.hatate.love.Love;
import inujini_.hatate.sqlite.dao.AccountDao;
import inujini_.hatate.sqlite.dao.SpellCardDao;
import inujini_.hatate.sqlite.dao.StatisticsDao;
import inujini_.hatate.util.IconUtil;
import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.volley.weather.WeatherAPI;
import inujini_.hatate.volley.weather.WeatherRequest;
import inujini_.hatate.volley.weather.WeatherResponse;
import inujini_.hatate.volley.yo.YoParam;
import inujini_.hatate.volley.yo.YoRequest;

import java.util.Random;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import twitter4j.TwitterException;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

/**
 * 包丁.
 */
@ExtensionMethod({PrefGetter.class})
public class Houtyou extends PierceReceiver {


	// These KILL fields do not use any longer.
	// But these need to switch from SharedPreference to SQLite, have been left.
	public static final String KEY_KILL = "kill";
	public static final String KEY_KILL_COUNT = "killCount";

	public static final String KEY_IS_PREVIEW = "preview";
	private static final boolean DEBUG = false;
	private static Resources _res;
	private static final int NOTIFY_WEATHER_REPORT = 100;

	@SuppressLint("DefaultLocale")
	@Override
	public void onPierced(final Context context, Intent intent) {
		val isPreview = intent.getBooleanExtra(KEY_IS_PREVIEW, false);

		if(!isPreview || DEBUG) {
			val pref = PreferenceManager.getDefaultSharedPreferences(context);
			pref.edit().putBoolean(MainActivity.KEY_GACHA, true).commit();

			// スペルカードの取得
			val spellCard = SpellCardDao.getRandomSpellCard(context);
			val spellCardName = spellCard.getName();

			val spellcardIntent = PendingIntent.getActivity(context
					, REQ_SPELL_CARD
					, new Intent(context, SpellCardHistoryActivity.class)
					, PendingIntent.FLAG_UPDATE_CURRENT);

			((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
				.notify(NOTIFY_SPELL_CARD
						, new NotificationCompat.Builder(context)
							.setSmallIcon(IconUtil.getIconId(spellCard.getCharacterId()))
							.setTicker(String.format("%s取得！", spellCardName))
							.setWhen(System.currentTimeMillis())
							.setContentTitle("スペルカード取得")
							.setContentText(String.format("はたてちゃんの頭部から%sを剥ぎ取りました。", spellCardName))
							.setContentIntent(spellcardIntent)
							.setAutoCancel(true)
							.build());

			// 統計情報
			val killCount = _statistics.getCount() + 1;
			int love = _statistics.getLove() + Love.culc(killCount);

			val time = String.format("[%02d:%02d]", context.getHour(), context.getMinute());
			if(time.equals("[00:00]")) love += 10;

			// Twitter連携
			if(context.isTweet()) {
				val res = getResources(context);
				val hash = res.getString(R.string.hash);

				String tweet = null;

				switch(new Random().nextInt(5)) {
				case 0:
					tweet = res.getString(R.string.houkoku1, killCount);
					break;
				case 1:
					tweet = res.getString(R.string.houkoku2);
					break;
				case 2:
					tweet = res.getString(R.string.houkoku3);
					break;
				case 3:
					tweet = res.getString(R.string.houkoku4, killCount);
					break;
				case 4:
					tweet = res.getString(R.string.houkoku5, spellCard.getName());
					break;
				}

				if(!DEBUG) {
					val twitters = AccountDao.getTwitter(context);
					if(twitters != null && !twitters.isEmpty()) {
						for (val twitter : twitters) {
							try {
								twitter.updateStatus(String.format("%s%s%s", time, tweet, hash));
							} catch (TwitterException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}

			// Yo
			if(context.isYo()) {
				val res = getResources(context);
				val param = new YoParam.Builder(res.getString(R.string.yo_api_key))
								.link(res.getString(R.string.yo_folk_url))
								.build();

				AppHatate.getRequestQueue(context).add(YoRequest.yoAll(param
					, new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							Log.d("Houtyou", "success yo all.");
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if(error.networkResponse == null) {
								Log.d("Houtyou"
									, String.format("error yo all. message:%s", error.getMessage()));
							} else {
								Log.d("Houtyou"
									, String.format("error yo all. statuscode:%d message:%s"
										, error.networkResponse.statusCode
										, error.getMessage()));
							}
						}
					}));
			}

			StatisticsDao.update(context, killCount, love);
		}
	}

	@Override
	protected void onUIThread(final Context context, Intent intent) {
		// 天気予報
		if(PrefGetter.isWeather(context)) {
			val locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			val criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			val provider = locationManager.getBestProvider(criteria, true);

			locationManager.requestLocationUpdates(provider, 0, 0, new LocationListener() {
				@Override
				public void onStatusChanged(String _, int status, Bundle extras) {
					// not implement
				}
				@Override
				public void onProviderEnabled(String _) {
					// not implement
				}
				@Override
				public void onProviderDisabled(String _) {
					// not implement
				}

				@Override
				public void onLocationChanged(Location location) {

					if(locationManager != null) locationManager.removeUpdates(this);

					val latitude = location.getLatitude();
					val longitude = location.getLongitude();

					AppHatate.getRequestQueue(context)
						.add(new WeatherRequest(WeatherAPI.Current, latitude, longitude, WeatherAPI.getKey(context)
								, new Listener<WeatherResponse>() {
								@Override
								public void onResponse(WeatherResponse response) {
									// 通知
									val notifyManager
										= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

									val notify = new NotificationCompat.Builder(context)
												.setSmallIcon(R.drawable.ic_launcher)
												.setTicker(String.format("%s : %s"
														, response.getName(), response.getWeather()))
												.setWhen(System.currentTimeMillis())
												.setContentTitle("はたてちゃん天気予報")
												.setContentText(String.format("今日の天気 ： %s\n最高気温:%.1f℃　最低気温：%.1f℃"
														, response.getWeather()
														, response.getMaxTemp()
														, response.getMinTemp()));

									notifyManager.notify(NOTIFY_WEATHER_REPORT, notify.build());
								}
							}, new ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								if(error.networkResponse == null) {
									Log.d("Houtyou"
										, String.format("error weather report. message:%s", error.getMessage()));
								} else {
									Log.d("Houtyou"
										, String.format("error weather report. statuscode:%d message:%s"
											, error.networkResponse.statusCode
											, error.getMessage()));
								}
							}
						}));

				}
			});
		}
	}

	private static Resources getResources(Context context) {
		if(_res != null) return _res;

		_res = context.getResources();
		return _res;
	}


}
