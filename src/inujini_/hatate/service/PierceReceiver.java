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
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

@ExtensionMethod({PrefGetter.class})
public abstract class PierceReceiver extends AsyncBroadcastReceiver {

	protected Statistics _statistics;

	@Override
	protected void asyncOnReceive(Context context, Intent intent) {

		_statistics = StatisticsDao.getStatistics(context);

		// バイブ
		if(context.isVibration()) {
			val vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(1000L);
		}

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

		val notify = new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_launcher)
						.setTicker("ウグゥーーーーーーーーーーッ!!!")
						.setWhen(System.currentTimeMillis())
						.setContentTitle("Hatate Houtyou Alarm")
						.setContentText("ウグゥーーーーーーーーーーッ!!!")
						.setContentIntent(PendingIntent
								.getService(context, -2, new Intent(context, Kill.class)
								, PendingIntent.FLAG_UPDATE_CURRENT));

		// LED
		if(context.isLight()) {
			notify.setLights((int) context.getLightColor(), 3000, 3000);
		}

		notifyManager.notify(0, notify.build());

		// スヌーズ
		if(context.isSnooze()) {
			Util.setSnooze(context);
		}

		onPierced(context, intent);
	}

	public abstract void onPierced(Context context, Intent intent);

}
