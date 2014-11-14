/**
* HatateHoutyouAlarm
*
* Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
*
* This software is released under the MIT License.
* http://opensource.org/licenses/mit-license.php
*/

package inujini_.hatate.sqlite.helper;

public final class ContentValuesExtensions {

	public static ContentValues putInt(ContentValues cv, ColumnProperty column, int value) {
		cv.put(column.getColumnName(), value);
		return cv;
	}

	public static ContentValues putBoolean(ContentValues cv, ColumnProperty column, boolean value) {
		cv.put(column.getColumnName(), (value ? 1: 0));
		return cv;
	}

	public static ContentValues putString(ContentValues cv, ColumnProperty column, String value) {
		cv.put(column.getColumnName(), value);
		return cv;
	}

	public static ContentValues putLong(ContentValues cv, ColumnProperty column, long value) {
		cv.put(column.getColumnName(), value);
		return cv;
	}

}
