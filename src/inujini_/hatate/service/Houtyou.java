package inujini_.hatate.service;

import java.util.Random;

import inujini_.function.Function.Func1;
import inujini_.hatate.R;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import inujini_.hatate.twitter.TwitterAccountDao;
import inujini_.hatate.util.PrefGetter;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import twitter4j.TwitterException;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

@ExtensionMethod({PrefGetter.class})
public class Houtyou extends BroadcastReceiver {

	public static final String KEY_KILL = "kill";
	public static final String KEY_KILL_COUNT = "killCount";
	public static final String KEY_IS_PREVIEW = "preview";

	@Override
	public void onReceive(Context context, Intent intent) {

		val isPreview = intent.getBooleanExtra(KEY_IS_PREVIEW, false);

		// バイブ
		if(context.isVibration()) {
			val vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(1000L);
		}

		// 声
		if(context.isScream()) {
			val mp = MediaPlayer.create(context, R.raw.ugu);
			mp.seekTo(0);
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
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
						.setContentText("ウグゥーーーーーーーーーーッ!!!");

		// LED
		if(context.isLight()) {
			notify.setLights((int) context.getLightColor(), 3000, 3000);
		}

		notifyManager.notify(0, notify.build());

		if(!isPreview) {
			val pref = context.getSharedPreferences(KEY_KILL, 0);
			pref.edit().putInt(KEY_KILL_COUNT, (pref.getInt(KEY_KILL_COUNT, 0) + 1)).commit();

			if(context.isTweet()) {
				new ReactiveAsyncTask<Context, Void, Void>(new Func1<Context, Void>() {
					@SuppressLint("DefaultLocale")
					@Override
					public Void call(Context x) {
						val res = x.getResources();
						val time = String.format("[%02d:%02d]", x.getHour(), x.getMinute());

						val hash = res.getString(R.string.hash);

						String tweet = null;
						val pref = x.getSharedPreferences(KEY_KILL, 0);

						switch(new Random().nextInt(4)) {
						case 0:
							tweet = res.getString(R.string.houkoku1, pref.getInt(KEY_KILL_COUNT, 0));
							break;
						case 1:
							tweet = res.getString(R.string.houkoku2);
							break;
						case 2:
							tweet = res.getString(R.string.houkoku3);
							break;
						case 3:
							tweet = res.getString(R.string.houkoku4, pref.getInt(KEY_KILL_COUNT, 0));
							break;
						}

						val twitter = TwitterAccountDao.getTwitter(x);
						try {
							twitter.updateStatus(String.format("%s%s%s", time, tweet, hash));
						} catch (TwitterException e) {
							e.printStackTrace();
						}
						return null;
					}
				}).execute(context);
			}
		}


	}

}
