package inujini_.hatate.love;

import inujini_.hatate.R;

import java.util.Random;

import lombok.val;
import android.util.Log;


public class Love {

	public static int init(int count) {
		return count * 2;
	}

	public static int getVoice(int love) {
		if(love >= 75) {
			Log.d("Love", "love");
			return R.raw.ugu_love;
		} else if (love < -50) {
			Log.d("Love", "hate");
			return R.raw.ugu_hate;
		} else {
			Log.d("Love", "normal");
			return R.raw.ugu;
		}
	}

	public static int culcLove(int killCount) {
		val rnd = new Random();
		val base = rnd.nextInt(255);
		int tmpLove = 0;

		if(base <= 100 && base != 0) {
			tmpLove = 2;
		} else if(base > 100 && base != 254) {
			tmpLove = -10;
		} else if(base == 0) {
			tmpLove = -100;
		} else if(base == 254){
			tmpLove = 10;
		}

		if(killCount < 5)
			return tmpLove;

		return rnd.nextBoolean() ? tmpLove * (killCount / 5): tmpLove;

	}

}
