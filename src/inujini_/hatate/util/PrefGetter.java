package inujini_.hatate.util;

import lombok.val;
import android.content.Context;
import android.preference.PreferenceManager;

public class PrefGetter {

	public static int getHour(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getInt("hour", 0);
	}

	public static int getMinute(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getInt("time", 0);
	}

	public static boolean isScream(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isScream", true);
	}

	public static boolean isVibration(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isVibration", true);
	}

	public static boolean isNoisy(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isNoisy", true);
	}

	public static boolean isLight(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isLight", true);
	}

	public static long getLightColor(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);

		// デフォルトはとりあえず赤
		return Long.decode(pref.getString("lightColor", "0xffff0000"));
	}

	public static String getLightColorName(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		val strColor = pref.getString("lightColor", "0xffff0000");
		return getLightColorName(strColor);
	}

	public static String getLightColorName(String strColor) {
		if(strColor.equals("0xffff0000")) {
			return "赤";
		} else if(strColor.equals("0xff0000ff")) {
			return "青";
		} else if(strColor.equals("0xff00ff00")) {
			return "緑";
		} else {
			throw new IllegalStateException(String.format("this lightColor is undifiend. %s", strColor));
		}
	}

	public static boolean isTweet(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isTweet", false);
	}

}
