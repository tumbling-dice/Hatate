package inujini_.hatate.util;

import inujini_.function.Function.Func1;
import inujini_.hatate.R;
import inujini_.linq.Linq;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.content.Context;
import android.preference.PreferenceManager;

@ExtensionMethod({Linq.class})
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
		return getLightColorName(context, strColor);
	}

	public static String getLightColorName(Context context, String strColor) {
		val res = context.getResources();
		val a = res.getStringArray(R.array.LightColorValues);

		for (int i = 0; i < a.length; i++) {
			if(strColor.equals(a[i]))
				return res.getStringArray(R.array.LightColorList)[i];
		}

		throw new IllegalStateException("lightColor couldn't find.");
	}

	public static boolean isTweet(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isTweet", false);
	}

	public static boolean isSnooze(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isSnooze", false);
	}

	public static int getSnoozeTime(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.parseInt(pref.getString("snoozeTime", "60"));
	}

	public static long getSnoozeTimeMill(Context context) {
		val time = getSnoozeTime(context);
		return (long) time * 1000;
	}

	public static long[] getVibrationPattern(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getString("vibrationPattern", "0,2000").split(",").linq().select(new Func1<String, Long>() {
			@Override
			public Long call(String x) {
				return Long.parseLong(x);
			}
		}).toLongArray();
	}

	public static String getVibrationPatternName(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		val str = pref.getString("vibrationPattern", "0,2000");
		return getVibrationPatternName(context, str);
	}

	public static String getVibrationPatternName(Context context, String pattern) {
		val res = context.getResources();
		val a = res.getStringArray(R.array.VibrationValues);

		for (int i = 0; i < a.length; i++) {
			if(pattern.equals(a[i]))
				return res.getStringArray(R.array.VibraitionList)[i];
		}

		throw new IllegalStateException("vibration pattern couldn't find.");
	}

}
