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

public class ColumnValuePair {

	private ColumnProperty column;
	private String value;

	public ColumnValuePair(ColumnProperty column, String value) {
		this.column = column;
		this.value = value;
	}

	public ColumnValuePair(ColumnProperty column, int value) {
		this.column = column;
		this.value = String.valueOf(value);
	}

	public ColumnValuePair(ColumnProperty column, long value) {
		this.column = column;
		this.value = String.valueOf(value);
	}

	public ColumnValuePair(ColumnProperty column, boolean value) {
		this.column = column;
		this.value = value ? "1" : "0";
	}

	public ColumnValuePair(ColumnProperty column, float value) {
		this.column = column;
		this.value = String.valueOf(value);
	}

	public ColumnValuePair(ColumnProperty column, double value) {
		this.column = column;
		this.value = String.valueOf(value);
	}

	public ColumnProperty getColumn() {
		return column;
	}

	public String getValue() {
		return value;
	}

}
