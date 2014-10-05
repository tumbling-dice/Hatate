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

}
