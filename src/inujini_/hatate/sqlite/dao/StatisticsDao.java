/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.sqlite.dao;

import inujini_.function.Function.Action1;
import inujini_.function.Function.Func1;
import inujini_.hatate.data.Statistics;
import inujini_.hatate.data.meta.MetaStatistics;
import inujini_.hatate.love.Love;
import inujini_.hatate.sqlite.DatabaseHelper;
import inujini_.sqlite.helper.ColumnValuePair;
import inujini_.sqlite.helper.CursorExtensions;
import inujini_.sqlite.helper.QueryBuilder;
import inujini_.sqlite.helper.SqliteUtil;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

@ExtensionMethod({SqliteUtil.class, CursorExtensions.class})
public class StatisticsDao {

	public static Statistics getStatistics(Context context) {
		val q = new QueryBuilder().selectAll().from(MetaStatistics.TBL_NAME).toString();
		return new DatabaseHelper(context).get(q, context, new Func1<Cursor, Statistics>() {
			@Override
			public Statistics call(Cursor x) {
				val s = new Statistics();
				s.setCount(Integer.parseInt(x.getStringByMeta(MetaStatistics.Count)));
				s.setLove(Integer.parseInt(x.getStringByMeta(MetaStatistics.Love)));
				return s;
			}
		});
	}

	public static void init(SQLiteDatabase db, int killCount) {
		val q = new QueryBuilder()
					.insert(MetaStatistics.TBL_NAME
							, new ColumnValuePair(MetaStatistics.Count, killCount)
							, new ColumnValuePair(MetaStatistics.Love, Love.init(killCount)))
					.toString();

		db.execSQL(q);
	}

	public static void update(Context context, int killCount, int love) {
		val q = new QueryBuilder()
				.update(MetaStatistics.TBL_NAME)
				.set(new ColumnValuePair(MetaStatistics.Count, killCount)
					, new ColumnValuePair(MetaStatistics.Love, love))
				.toString();

		new DatabaseHelper(context).transaction(context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase db) {
				db.execSQL(q);
			}
		});
	}
}
