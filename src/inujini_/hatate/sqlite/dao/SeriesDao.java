/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.sqlite.dao;

import inujini_.hatate.data.Series;
import inujini_.hatate.data.meta.MetaSeries;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.function.Function.Predicate;
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
 * {@link Series}のDAO.
 */
@ExtensionMethod({SqliteUtil.class, CursorExtensions.class, Linq.class})
public class SeriesDao {

	private static final Func1<Cursor, Series> _converter = new Func1<Cursor, Series>() {
		@Override
		public Series call(Cursor c) {
			val series = new Series();
			series.setId(c.getLongByMeta(MetaSeries.Id));
			series.setName(c.getStringByMeta(MetaSeries.Name));
			return series;
		}
	};

	/**
	 * 全シリーズの取得.
	 * @param context
	 * @return DBに登録されている全ての{@link Series}.
	 */
	public static List<Series> getAllSeries(Context context) {
		val q = new QueryBuilder()
					.selectAll()
					.from(MetaSeries.TBL_NAME)
					.toString();

		return new DatabaseHelper(context).getList(q, context, _converter);
	}

	/**
	 * シリーズの取得.
	 * @param context
	 * @param id シリーズID
	 * @return idで指定された{@link Series}.
	 * @see getSerieses(Context, long[])
	 */
	public static Series getSeries(Context context, long id) {
		val q = new QueryBuilder()
					.selectAll()
					.from(MetaSeries.TBL_NAME)
					.where().equal(MetaSeries.Id, id)
					.toString();

		return new DatabaseHelper(context).get(q, context, _converter);
	}

	/**
	 * シリーズの取得.
	 * @param context
	 * @param ids シリーズIDの配列
	 * @return idsで指定された{@link Series}のリスト.
	 * @see getSeries(Context, long)
	 */
	public static List<Series> getSerieses(Context context, final long[] ids) {
		return getAllSeries(context).linq().where(new Predicate<Series>() {
			@Override
			public Boolean call(Series x) {
				val id = x.getId();
				for (val l : ids) {
					if(id == l) return true;
				}

				return false;
			}
		}).toList();
	}

}
