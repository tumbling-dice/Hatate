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
 * Twitterアカウント情報.
 *
 * @see MetaAccount
 */
@SqliteTable(value="Account",hasPrimaryId=true)
@Data
public class TwitterAccount implements ISqlite, Serializable {

	private static final long serialVersionUID = -5638034687131322024L;

	@SqliteField(name="AccessToken", type=FIELD_TEXT, notNull=true)
	private String accessToken;
	@SqliteField(name="AccessSecret", type=FIELD_TEXT, notNull=true)
	private String accessSecret;
	@SqliteField(name="UserId", type=FIELD_TEXT, notNull=true)
	private long userId;
	@SqliteField(name="ScreenName", type=FIELD_TEXT, notNull=true)
	private String screenName;
	@SqliteField(name="UseFlag", type=FIELD_INTEGER, defaultValue="1")
	private boolean isUse;

}
