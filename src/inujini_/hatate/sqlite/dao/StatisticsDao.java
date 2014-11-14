/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.sqlite.dao;

import inujini_.hatate.data.Statistics;
import inujini_.hatate.data.meta.MetaStatistics;
import inujini_.hatate.function.Function.Action1;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.love.Love;
import inujini_.hatate.sqlite.DatabaseHelper;
import inujini_.hatate.sqlite.helper.ColumnValuePair;
import inujini_.hatate.sqlite.helper.CursorExtensions;
import inujini_.hatate.sqlite.helper.QueryBuilder;
import inujini_.hatate.sqlite.helper.SqliteUtil;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * {@link Statistics}のDAO.
 */
@ExtensionMethod({SqliteUtil.class, CursorExtensions.class})
public class StatisticsDao {

	/**
	 * 統計情報取得
	 * @param context
	 * @return {@link Statistics}
	 */
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

	/**
	 * 初期化
	 * @param db
	 * @param killCount
	 */
	public static void init(SQLiteDatabase db, int killCount) {
		val q = new QueryBuilder()
					.insert(MetaStatistics.TBL_NAME
							, new ColumnValuePair(MetaStatistics.Count, killCount)
							, new ColumnValuePair(MetaStatistics.Love, Love.init(killCount)))
					.toString();

		db.execSQL(q);
	}

	/**
	 * 更新
	 * @param context
	 * @param killCount
	 * @param love
	 */
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
