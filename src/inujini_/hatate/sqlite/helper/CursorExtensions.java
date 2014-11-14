/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.sqlite.helper;

import inujini_.sqlite.meta.ColumnProperty;
import android.database.Cursor;

/**
 * {@link Cursor}拡張.
 */
public final class CursorExtensions {

	/**
	 * ColumnPropertyからintでデータを取得.
	 * @param c
	 * @param property
	 * @return propertyのカラムから取得したint値
	 */
	public static int getIntByMeta(Cursor c, ColumnProperty property) {
		return c.getInt(c.getColumnIndex(property.getColumnName().toString()));
	}

	/**
	 * ColumnPropertyからStringでデータを取得.
	 * @param c
	 * @param property
	 * @return propertyのカラムから取得したString値
	 */
	public static String getStringByMeta(Cursor c, ColumnProperty property) {
		return c.getString(c.getColumnIndex(property.getColumnName().toString()));
	}

	/**
	 * ColumnPropertyからlongでデータを取得.
	 * @param c
	 * @param property
	 * @return propertyのカラムから取得したlong値
	 */
	public static long getLongByMeta(Cursor c, ColumnProperty property) {
		return c.getLong(c.getColumnIndex(property.getColumnName().toString()));
	}

	/**
	 * ColumnPropertyからfloatでデータを取得.
	 * @param c
	 * @param property
	 * @return propertyのカラムから取得したfloat値
	 */
	public static float getFloatByMeta(Cursor c, ColumnProperty property) {
		return c.getFloat(c.getColumnIndex(property.getColumnName().toString()));
	}

	/**
	 * ColumnPropertyからbooleanでデータを取得.
	 * @param c
	 * @param property
	 * @return propertyのカラムから取得したboolean値
	 */
	public static boolean getBooleanByMeta(Cursor c, ColumnProperty property) {
		return getIntByMeta(c, property) == 1;
	}
}
