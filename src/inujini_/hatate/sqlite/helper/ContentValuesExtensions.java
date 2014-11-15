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
import android.content.ContentValues;

/**
 * {@link ContentValues}拡張.
 */
public final class ContentValuesExtensions {

	/**
	 * intの登録.
	 * @param cv
	 * @param column
	 * @param value
	 * @return
	 */
	public static ContentValues putInt(ContentValues cv, ColumnProperty column, int value) {
		cv.put(column.getColumnName(), value);
		return cv;
	}

	/**
	 * booleanの登録.
	 * @param cv
	 * @param column
	 * @param value
	 * @return
	 */
	public static ContentValues putBoolean(ContentValues cv, ColumnProperty column, boolean value) {
		cv.put(column.getColumnName(), (value ? 1: 0));
		return cv;
	}

	/**
	 * Stringの登録.
	 * @param cv
	 * @param column
	 * @param value
	 * @return
	 */
	public static ContentValues putString(ContentValues cv, ColumnProperty column, String value) {
		cv.put(column.getColumnName(), value);
		return cv;
	}

	/**
	 * longの登録.
	 * @param cv
	 * @param column
	 * @param value
	 * @return
	 */
	public static ContentValues putLong(ContentValues cv, ColumnProperty column, long value) {
		cv.put(column.getColumnName(), value);
		return cv;
	}

}
