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
 * �L�����f�[�^.
 *
 * @see MetaCharacter
 */
@Data
@SqliteTable("Character")
public class Character implements Serializable, ISqlite {

	private static final long serialVersionUID = 7909552232097654090L;

	@SqliteField(name="Id", type=FIELD_INTEGER, primary=true)
	private long id;

	@SqliteField(name="Name", type=FIELD_TEXT, notNull=true, unique=true)
	private String name;
}