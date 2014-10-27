/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import inujini_.hatate.love.Love;
import inujini_.hatate.sqlite.dao.StatisticsDao;
import inujini_.hatate.util.PrefGetter;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

/**
 * スペルカードガチャ画面.
 */
@ExtensionMethod({PrefGetter.class})
public class GachaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gacha);
	}

	public void gacha(View v) {
		val context = getApplicationContext();

		if(context.isVibration()) {
			val vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibe.vibrate(context.getVibrationPattern(), -1);
		}

		if(context.isScream()) {
			val statistics = StatisticsDao.getStatistics(context);
			val mp = MediaPlayer.create(context, Love.getVoice(statistics.getLove()));
			mp.seekTo(0);
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer x) {
					x.release();
				}
			});
		}

		setResult(RESULT_OK);
		finish();
	}
}
