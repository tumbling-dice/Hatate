package inujini_.hatate.service;

import inujini_.hatate.R;
import inujini_.hatate.love.Love;
import inujini_.hatate.sqlite.dao.AccountDao;
import inujini_.hatate.sqlite.dao.StatisticsDao;
import inujini_.hatate.util.PrefGetter;

import java.util.Random;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import twitter4j.TwitterException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

@ExtensionMethod({PrefGetter.class})
public class Houtyou extends PierceReceiver {

	// These KILL fields do not use any longer.
	// But these need to switch from SharedPreference to SQLite, have been left.
	public static final String KEY_KILL = "kill";
	public static final String KEY_KILL_COUNT = "killCount";

	public static final String KEY_IS_PREVIEW = "preview";
	private static final boolean DEBUG = false;

	@SuppressLint("DefaultLocale")
	@Override
	public void onPierced(Context context, Intent intent) {
		val isPreview = intent.getBooleanExtra(KEY_IS_PREVIEW, false);

		if(!isPreview || DEBUG) {
			val killCount = _statistics.getCount() + 1;
			int love = _statistics.getLove() + Love.culcLove(killCount);

			val time = String.format("[%02d:%02d]", context.getHour(), context.getMinute());
			if(time.equals("[00:00]")) love += 10;

			// Twitter連携
			if(context.isTweet()) {
				val res = context.getResources();
				val hash = res.getString(R.string.hash);

				String tweet = null;

				switch(new Random().nextInt(4)) {
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
				}

				val twitter = AccountDao.getTwitter(context);
				try {
					twitter.updateStatus(String.format("%s%s%s", time, tweet, hash));
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}

			StatisticsDao.update(context, killCount, love);
		}
	}

}
