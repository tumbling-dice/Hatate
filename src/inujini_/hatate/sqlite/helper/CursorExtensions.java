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

public final class CursorExtensions {
	public static int getIntByMeta(Cursor c, ColumnProperty property) {
		return c.getInt(c.getColumnIndex(property.getColumnName().toString()));
	}

	public static String getStringByMeta(Cursor c, ColumnProperty property) {
		return c.getString(c.getColumnIndex(property.getColumnName().toString()));
	}

	public static long getLongByMeta(Cursor c, ColumnProperty property) {
		return c.getLong(c.getColumnIndex(property.getColumnName().toString()));
	}

	public static float getFloatByMeta(Cursor c, ColumnProperty property) {
		return c.getFloat(c.getColumnIndex(property.getColumnName().toString()));
	}

	public static boolean getBooleanByMeta(Cursor c, ColumnProperty property) {
		int v = getIntByMeta(c, property);
		return v == 1;
	}
}
