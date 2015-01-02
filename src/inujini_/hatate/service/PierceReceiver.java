/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.service;

import inujini_.hatate.R;
import inujini_.hatate.data.Statistics;
import inujini_.hatate.love.Love;
import inujini_.hatate.sqlite.dao.StatisticsDao;
import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.util.Util;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.support.v4.app.NotificationCompat;

/**
 * 通知処理の共通部分.
 * @see Houtyou
 * @see OneMoreLovely
 */
@ExtensionMethod({PrefGetter.class})
public abstract class PierceReceiver extends AsyncBroadcastReceiver {

	protected Statistics _statistics;
	public static final int NOTIFY_HATATE_HOUTYOU = 0;
	public static final int NOTIFY_SPELL_CARD = 1;
	public static final int REQ_KILL = 10;
	public static final int REQ_SPELL_CARD = 11;

	@Override
	protected void asyncOnReceive(Context context, Intent intent) {
		Util.dbUpdate(context);

		_statistics = StatisticsDao.getStatistics(context);

		// 声
		if(context.isScream()) {
			val mp = MediaPlayer.create(context, Love.getVoice(_statistics.getLove()));
			mp.seekTo(0);
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer x) {
					x.release();
				}
			});
		}

		// 通知
		val notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		val killIntent = PendingIntent.getService(context, REQ_KILL, new Intent(context, Kill.class)
				, PendingIntent.FLAG_UPDATE_CURRENT);

		val notify = new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_launcher)
						.setTicker("ウグゥーーーーーーーーーーッ!!!")
						.setWhen(System.currentTimeMillis())
						.setContentTitle("Hatate Houtyou Alarm")
						.setContentText("ウグゥーーーーーーーーーーッ!!!")
						.setContentIntent(killIntent)
						.setDeleteIntent(killIntent);

		// LED
		if(context.isLight()) {
			notify.setLights((int) context.getLightColor(), 3000, 3000);
		}

		// バイブ
		if(context.isVibration()) {
			notify.setVibrate(context.getVibrationPattern());
		}

		notifyManager.notify(NOTIFY_HATATE_HOUTYOU, notify.build());

		// スヌーズ
		if(context.isSnooze()) {
			Util.setSnooze(context);
		}

		onAfterPierced(context, intent);
	}

	/**
	 * 通知処理が終わった後の処理.
	 * @param context
	 * @param intent
	 */
	public abstract void onAfterPierced(Context context, Intent intent);

}
