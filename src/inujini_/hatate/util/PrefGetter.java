/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.util;

import inujini_.hatate.R;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.linq.Linq;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.content.Context;
import android.preference.PreferenceManager;

/**
 * PreferenceActivityで設定された各値を取得するためのUtility class.
 * @see PreferenceManager#getDefaultSharedPreferences(Context)
 */
@ExtensionMethod({Linq.class})
public class PrefGetter {

	/**
	 * 起動時刻（単位：時）の取得.
	 * @param context
	 * @return key:[hour] default:0
	 */
	public static int getHour(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getInt("hour", 0);
	}

	/**
	 * 起動時刻（単位：分）の取得.
	 * @param context
	 * @return key:[time] default:0
	 */
	public static int getMinute(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getInt("time", 0);
	}

	/**
	 * 悲鳴の実行有無.
	 * @param context
	 * @return key:[isScream] default:true
	 */
	public static boolean isScream(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isScream", true);
	}

	/**
	 * バイブ機能の実行有無.
	 * @param context
	 * @return key:[isVibration] default:true
	 */
	public static boolean isVibration(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isVibration", true);
	}

	/**
	 * 通知機能の実行有無.
	 * @param context
	 * @return key:[isNoisy] default:true
	 */
	public static boolean isNoisy(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isNoisy", true);
	}

	/**
	 * LEDの点灯有無.
	 * @param context
	 * @return key:[isLight] default:true
	 */
	public static boolean isLight(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isLight", true);
	}

	/**
	 * <p>LEDのカラーコード(ARGB)取得.</p>
	 * <p>カラーコードはlongにデコードされた値となる.</p>
	 * @param context
	 * @return key:[lightColor] default:0xffff0000 (red)
	 * @see Long#decode(String)
	 */
	public static long getLightColor(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return Long.decode(pref.getString("lightColor", "0xffff0000"));
	}

	/**
	 * LEDの色名取得.
	 * @param context
	 * @return {@link R#array#LightColorValues}からカラーコードの一覧を検索し、ヒットした位置の{@link R#array#LightColorList}の値.
	 * @see #getLightColorName(Context, String)
	 */
	public static String getLightColorName(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		val strColor = pref.getString("lightColor", "0xffff0000");
		return getLightColorName(context, strColor);
	}

	/**
	 * LEDの色名取得.
	 * @param context
	 * @param strColor カラーコード (ARGB)
	 * @return {@link R#array#LightColorValues}からカラーコードの一覧を検索し、ヒットした位置の{@link R#array#LightColorList}の値.
	 * @throws IllegalStateException strColorが{@link R#array#LightColorValues}内に存在しなかった場合に発生.
	 */
	public static String getLightColorName(Context context, String strColor) {
		val res = context.getResources();
		val a = res.getStringArray(R.array.LightColorValues);

		for (int i = 0; i < a.length; i++) {
			if(strColor.equals(a[i]))
				return res.getStringArray(R.array.LightColorList)[i];
		}

		throw new IllegalStateException("lightColor couldn't find.");
	}

	/**
	 * Twitter連携機能の実行有無.
	 * @param context
	 * @return key:[isTweet] default:false
	 */
	public static boolean isTweet(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isTweet", false);
	}

	/**
	 * スヌーズ機能の実行有無.
	 * @param context
	 * @return key:[isSnooze] default:false
	 */
	public static boolean isSnooze(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isSnooze", false);
	}

	/**
	 * スヌーズの間隔を取得（単位：秒）.
	 * @param context
	 * @return key:[snoozeTime] default:60
	 * @see #getSnoozeTimeMill(Context)
	 */
	public static int getSnoozeTime(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.parseInt(pref.getString("snoozeTime", "60"));
	}

	/**
	 * スヌーズの間隔を取得（単位：ミリ秒）.
	 * @param context
	 * @return (key:[snoozeTime] default:60) の1000倍
	 * @see #getSnoozeTime(Context)
	 */
	public static long getSnoozeTimeMill(Context context) {
		val time = getSnoozeTime(context);
		return (long) time * 1000;
	}

	/**
	 * バイブレーションパターン取得.
	 * @param context
	 * @return key:[vibrationPattern] default:0,2000
	 */
	public static long[] getVibrationPattern(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getString("vibrationPattern", "0,2000").split(",").linq().select(new Func1<String, Long>() {
			@Override
			public Long call(String x) {
				return Long.parseLong(x);
			}
		}).toLongArray();
	}

	/**
	 * バイブレーションパターン名取得.
	 * @param context
	 * @return key:[vibrationPattern] default:0,2000
	 * @see #getVibrationPatternName(Context, String)
	 */
	public static String getVibrationPatternName(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		val str = pref.getString("vibrationPattern", "0,2000");
		return getVibrationPatternName(context, str);
	}

	/**
	 * LEDの色名取得.
	 * @param context
	 * @param pattern
	 * @return {@link R#array#VibrationValues}からパターンを検索し、ヒットした位置の{@link R#array#VibraitionList}の値.
	 * @throws IllegalStateException patternが{@link R#array#VibrationValues}内に存在しなかった場合に発生.
	 */
	public static String getVibrationPatternName(Context context, String pattern) {
		val res = context.getResources();
		val a = res.getStringArray(R.array.VibrationValues);

		for (int i = 0; i < a.length; i++) {
			if(pattern.equals(a[i]))
				return res.getStringArray(R.array.VibraitionList)[i];
		}

		throw new IllegalStateException("vibration pattern couldn't find.");
	}

	/**
	 * スペルカードガチャ可能フラグ.
	 * @param context
	 * @return key:[canGacha] default:true
	 */
	public static boolean canGacha(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("canGacha", true);
	}

	/**
	 * YoAll送信フラグ.
	 * @param context
	 * @return key:[isYo] default:false
	 */
	public static boolean isYo(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("isYo", false);
	}

	/**
	 * Yoのユーザ名取得
	 * @param context
	 * @return key:[yo] default:null
	 */
	public static String getYo(Context context) {
		val pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getString("yo", null);
	}

}
