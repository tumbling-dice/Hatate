/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.data;

import inujini_.sqlite.meta.ISqlite;
import inujini_.sqlite.meta.annotation.SqliteField;
import inujini_.sqlite.meta.annotation.SqliteTable;

import java.io.Serializable;

import lombok.Data;

/**
 * 統計情報.
 * @see MetaStatistics
 */
@Data
@SqliteTable("Statistics")
public class Statistics implements Serializable, ISqlite {

	private static final long serialVersionUID = 5624460324478653199L;

	@SqliteField(name="Count", type=FIELD_TEXT, defaultValue="0")
	private int count;

	@SqliteField(name="Love", type=FIELD_TEXT, defaultValue="0")
	private int love;


}
