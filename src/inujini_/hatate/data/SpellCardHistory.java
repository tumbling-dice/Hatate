/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.data;

import java.io.Serializable;

import inujini_.sqlite.meta.ISqlite;
import inujini_.sqlite.meta.annotation.SqliteField;
import inujini_.sqlite.meta.annotation.SqliteTable;
import lombok.Data;

/**
 * スペルカード取得履歴.
 *
 * @see MetaSpellCardHistory
 */
@Data
@SqliteTable("SpellCardHistory")
public class SpellCardHistory implements Serializable, ISqlite {
	private static final long serialVersionUID = 5303526450785933435L;

	@SqliteField(name="Id", type=FIELD_INTEGER, notNull=true)
	private long id;

	@SqliteField(name="Name", type=FIELD_TEXT, notNull=true)
	private String name;

	@SqliteField(name="Timestamp", type=FIELD_TEXT, notNull=true)
	private String timestamp;

	@SqliteField(name="HistoryOrder", type=FIELD_INTEGER, primary=true, autoincrement=true)
	private int order;
}
