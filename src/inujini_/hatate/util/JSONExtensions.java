/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.util;

import inujini_.hatate.function.Function.Func1;

import java.util.ArrayList;
import java.util.List;

import lombok.val;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JSONExtensions {

	@SuppressWarnings("unchecked")
	// FIXME map -> mapToList
	public static <T, R> List<R> map(JSONArray json, Func1<T, R> callback) throws JSONException {
		val list = new ArrayList<R>();
		for (int i = 0, length = json.length(); i < length; i++) {
			list.add(callback.call((T) json.get(i)));
		}

		return list;
	}

	public static <T, R> R[] mapToArray(JSONArray json, R[] rs, Func1<T, R> callback) throws JSONException {
		return map(json, callback).toArray(rs);
	}

	public static String[] toStringArray(JSONArray json) throws JSONException {
		val length = json.length();
		val items = new String[length];
		for (int i = 0; i < length; i++) {
			items[i] = json.getString(i);
		}
		return items;
	}

	public static int[] toIntArray(JSONArray json) throws JSONException {
		val length = json.length();
		val items = new int[length];
		for (int i = 0; i < length; i++) {
			items[i] = json.getInt(i);
		}
		return items;
	}

	public static String getStringOrDefault(JSONObject item, String name) throws JSONException {
		return getStringOrDefault(item, name, null);
	}

	public static String getStringOrDefault(JSONObject item, String name, String defaultValue) throws JSONException {
		if(item.isNull(name)) return null;
		val v = item.getString(name);
		return v.equals("") ? defaultValue : v;
	}

	public static long getLongOrDefault(JSONObject item, String name) throws JSONException {
		return getLongOrDefault(item, name, 0L);
	}

	public static long getLongOrDefault(JSONObject item, String name, long defaultValue) throws JSONException {
		if(item.isNull(name)) return defaultValue;
		return item.getLong(name);
	}

	public static int getIntOrDefault(JSONObject item, String name) throws JSONException {
		return getIntOrDefault(item, name, 0);
	}

	public static int getIntOrDefault(JSONObject item, String name, int defaultValue) throws JSONException {
		if(item.isNull(name)) return defaultValue;
		return item.getInt(name);
	}

	public static double getDoubleOrDefault(JSONObject item, String name) throws JSONException {
		return getDoubleOrDefault(item, name, 0.0);
	}

	public static double getDoubleOrDefault(JSONObject item, String name, double defaultValue) throws JSONException {
		if(item.isNull(name)) return defaultValue;
		return item.getDouble(name);
	}

}
