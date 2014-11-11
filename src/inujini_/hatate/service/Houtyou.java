/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.service;

import inujini_.hatate.MainActivity;
import inujini_.hatate.R;
import inujini_.hatate.SpellCardHistoryActivity;
import inujini_.hatate.love.Love;
import inujini_.hatate.sqlite.dao.AccountDao;
import inujini_.hatate.sqlite.dao.SpellCardDao;
import inujini_.hatate.sqlite.dao.StatisticsDao;
import inujini_.hatate.util.IconUtil;
import inujini_.hatate.util.PrefGetter;

import java.util.Random;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import twitter4j.TwitterException;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/**
 *
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

	@SuppressLint("DefaultLocale")
	@Override
	public void onPierced(Context context, Intent intent) {
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
				AppHatate.getRequestQueue()
					.add(YoRequest.yoAll(new YoParam(getResources(context).getString(R.string.yo_api_key))
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
											, error.networkResponse.statuscode
											, error.getMessage()));
								}
							}
						}));
			}
			

			StatisticsDao.update(context, killCount, love);
		}
	}
	
	private static Resources getResources(Context context) {
		if(_res != null) return _res;
		
		_res = context.getResources();
		return _res;
	}
}
