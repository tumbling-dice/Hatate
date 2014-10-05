package inujini_.hatate.service;

import inujini_.hatate.R;
import inujini_.hatate.util.PrefGetter;
import lombok.val;
import lombok.experimental.ExtensionMethod;
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

	@Override
	public void onReceive(Context context, Intent intent) {

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

		notifyManager.notify(0, notify.build());
	}

}
