/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.sqlite.dao;

import inujini_.hatate.data.Character;
import inujini_.hatate.data.meta.MetaCharacter;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.linq.Linq;
import inujini_.hatate.sqlite.DatabaseHelper;
import inujini_.hatate.sqlite.helper.CursorExtensions;
import inujini_.hatate.sqlite.helper.QueryBuilder;
import inujini_.hatate.sqlite.helper.SqliteUtil;

import java.util.List;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.content.Context;
import android.database.Cursor;

/**
 *
 */
@ExtensionMethod({SqliteUtil.class, CursorExtensions.class, Linq.class})
public class CharacterDao {

	private static final Func1<Cursor, Character> _converter = new Func1<Cursor, Character>() {
		@Override
		public Character call(Cursor c) {
			val character = new Character();
			character.setId(c.getLongByMeta(MetaCharacter.Id));
			character.setName(c.getStringByMeta(MetaCharacter.Name));
			return character;
		}
	};

	public static List<Character> getAllCharacter(Context context) {
		val q = new QueryBuilder()
					.selectAll()
					.from(MetaCharacter.TBL_NAME)
					.toString();

		return new DatabaseHelper(context).getList(q, context, _converter);
	}

	public static Character getCharacter(Context context, long id) {
		val q = new QueryBuilder()
					.selectAll()
					.from(MetaCharacter.TBL_NAME)
					.where().equal(MetaCharacter.Id, id)
					.toString();

		return new DatabaseHelper(context).get(q, context, _converter);
	}


}
