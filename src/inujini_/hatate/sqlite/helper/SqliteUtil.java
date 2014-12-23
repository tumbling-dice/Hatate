/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.sqlite.helper;

import inujini_.hatate.function.Function.Action1;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.function.Function.Predicate;
import inujini_.hatate.linq.Linq;
import inujini_.sqlite.meta.ISqlite;
import inujini_.sqlite.meta.annotation.SqliteField;
import inujini_.sqlite.meta.annotation.SqliteTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@ExtensionMethod({Linq.class})
public class SqliteUtil {

	public static interface ErrorHandler {
		public boolean onError(SQLiteDatabase db, RuntimeException e);
		public void recovery(SQLiteDatabase db);
	}

	/**
	 * DBから要素を取得
	 * @param helper
	 * @param query Select文
	 * @param context
	 * @param f CursorからTを抽出するFunc
	 * @return 要素が見つからない場合はNull
	 */
	public static <T> T get(SQLiteOpenHelper helper, String query, Context context, Func1<Cursor, T> f) {

		SQLiteDatabase db = helper.getReadableDatabase();

		if(!isMainThread(context)) db.acquireReference();

		Cursor c = null;

		try {
			c = db.rawQuery(query, null);
		} catch(RuntimeException e) {
			db.close();
			throw e;
		}

		if(!c.moveToFirst()) {
			c.close();
			helper.close();
			return null;
		}

		T obj;

		try {
			obj = f.call(c);
		} finally {
			c.close();
			helper.close();
		}

		return obj;
	}

	/**
	 * DBからすべての要素を取得
	 * @param helper
	 * @param query Select文
	 * @param f CursorからTを抽出するFunc
	 * @param context
	 * @return 要素が見つからない場合は空のリスト
	 */
	public static <T> List<T> getList(SQLiteOpenHelper helper, String query, Context context, Func1<Cursor, T> f) {

		List<T> dataList = new ArrayList<T>();

		SQLiteDatabase db = helper.getReadableDatabase();
		if(!isMainThread(context)) db.acquireReference();

		Cursor c = null;
		try {
			c = db.rawQuery(query, null);
		} catch(RuntimeException e) {
			helper.close();
			throw e;
		}

		if(!c.moveToFirst()) {
			c.close();
			helper.close();
			return dataList;
		}

		try {
			do {
				dataList.add(f.call(c));
			} while (c.moveToNext());
		} finally {
			c.close();
			helper.close();
		}

		return dataList;
	}

	public static <K, V> Map<K, V> getMap(SQLiteOpenHelper helper, String query, Context context
			, Func1<Cursor, K> keyProvider, Func1<Cursor, V> valueProvider) {

		Map<K, V> map = new HashMap<K, V>();

		SQLiteDatabase db = helper.getReadableDatabase();
		if(!isMainThread(context)) db.acquireReference();

		Cursor c = null;
		try {
			c = db.rawQuery(query, null);
		} catch(RuntimeException e) {
			helper.close();
			throw e;
		}

		if(!c.moveToFirst()) {
			c.close();
			helper.close();
			return map;
		}

		try {
			do {
				map.put(keyProvider.call(c), valueProvider.call(c));
			} while (c.moveToNext());
		} finally {
			c.close();
			helper.close();
		}

		return map;

	}

	/**
	 * トランザクション処理
	 * @param helper
	 * @param context
	 * @param onTransaction
	 */
	public static void transaction(SQLiteOpenHelper helper, Context context, Action1<SQLiteDatabase> onTransaction) {
		transaction(helper, context, onTransaction, null);
	}

	/**
	 * トランザクション処理
	 * @param helper
	 * @param context
	 * @param onTransaction 実行内容
	 * @param errorHandler
	 */
	public static void transaction(SQLiteOpenHelper helper, Context context, Action1<SQLiteDatabase> onTransaction
		, ErrorHandler errorHandler) {

		SQLiteDatabase db = helper.getWritableDatabase();
		if(!isMainThread(context)) db.acquireReference();

		db.beginTransaction();
		try {
			onTransaction.call(db);
			if(db.isOpen()) db.setTransactionSuccessful();
		} catch(RuntimeException e) {
			if(errorHandler == null || !errorHandler.onError(db, e)) {
				throw e;
			}
		} finally {
			if(errorHandler != null) errorHandler.recovery(db);
			if(db.isOpen()) {
				db.endTransaction();
				helper.close();
			}
		}
	}

	/**
	 * SELECT *
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param converter 取得したカーソルの変換
	 * @return
	 */
	public static <R> List<R> selectAll(SQLiteOpenHelper helper, Context context, String tableName
		, Func1<Cursor, R> converter) {
		return getList(helper, new QueryBuilder().selectAll().from(tableName).toString()
			, context, converter);
	}

	/**
	 * INSERT
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param columns INSERTするカラムと値のペア
	 */
	public static void insert(SQLiteOpenHelper helper, Context context, String tableName
		, ColumnValuePair... columns) {
		insert(helper, context, tableName, columns, null);
	}

	/**
	 * INSERT
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param cv INSERTする値
	 */
	public static void insert(SQLiteOpenHelper helper, Context context, String tableName
		, ContentValues cv) {
		insert(helper, context, tableName, cv, null);
	}

	/**
	 * INSERT
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param columns INSERTするカラムと値のペア
	 * @param errorHandler エラー時処理
	 */
	public static void insert(SQLiteOpenHelper helper, Context context, final String tableName
		, final ColumnValuePair[] columns, ErrorHandler errorHandler) {

		val cv = new ContentValues();
		for(val c : columns) {
			val v = c.getValue();
			if(v != null) cv.put(c.getColumn().getColumnName(), v);
		}
		insert(helper, context, tableName, columns, errorHandler);
	}

	/**
	 * INSERT
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param cv INSERTする値
	 * @param errorHandler エラー時処理
	 */
	public static void insert(SQLiteOpenHelper helper, Context context, final String tableName
		, final ContentValues cv, ErrorHandler errorHandler) {
		transaction(helper, context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase db) {
				db.insert(tableName, null, cv);
			}
		}, errorHandler);
	}

	/**
	 * REPLACE
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param columns INSERT or UPDATEするカラムと値のペア
	 */
	public static void replace(SQLiteOpenHelper helper, Context context, String tableName
		, ColumnValuePair... columns) {
		replace(helper, context, tableName, columns, null);
	}

	/**
	 * REPLACE
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param cv INSERT or UPDATEする値
	 */
	public static void replace(SQLiteOpenHelper helper, Context context, String tableName
		, ContentValues cv) {
		replace(helper, context, tableName, cv, null);
	}

	/**
	 * REPLACE
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param columns INSERT or UPDATEするカラムと値のペア
	 * @param errorHandler エラー時処理
	 */
	public static void replace(SQLiteOpenHelper helper, Context context, final String tableName
		, final ColumnValuePair[] columns, ErrorHandler errorHandler) {
		val cv = new ContentValues();
		for(val c : columns) {
			val v = c.getValue();
			if(v != null) cv.put(c.getColumn().getColumnName(), v);
		}
		replace(helper, context, tableName, columns, errorHandler);
	}

	/**
	 * REPLACE
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param cv INSERT or UPDATEする値
	 * @param errorHandler エラー時処理
	 */
	public static void replace(SQLiteOpenHelper helper, Context context, final String tableName
		, final ContentValues cv, ErrorHandler errorHandler) {
		transaction(helper, context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase db) {
				db.replace(tableName, null, cv);
			}
		}, errorHandler);
	}

	/**
	 * UPDATE
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param updateColumns UPDATEするカラムと値のペア
	 * @param whereColumns WHEREの条件とするカラムと値のペア
	 */
	public static void update(SQLiteOpenHelper helper, Context context, String tableName
		, ColumnValuePair[] updateColumns, ColumnValuePair... whereColumns) {
		update(helper, context, tableName, updateColumns, whereColumns, null);
	}

	/**
	 * UPDATE
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param cv UPDATEする値
	 * @param whereColumns WHEREの条件とするカラムと値のペア
	 */
	public static void update(SQLiteOpenHelper helper, Context context, String tableName
		, ContentValues cv, ColumnValuePair... whereColumns) {
		update(helper, context, tableName, cv, whereColumns, null);
	}

	/**
	 * UPDATE
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param updateColumns UPDATEするカラムと値のペア
	 * @param whereColumns WHEREの条件とするカラムと値のペア
	 * @param errorHandler エラー時処理
	 */
	public static void update(SQLiteOpenHelper helper, Context context, final String tableName
		, final ColumnValuePair[] updateColumns, final ColumnValuePair[] whereColumns
		, ErrorHandler errorHandler) {
		val cv = new ContentValues();
		for(val c : updateColumns) {
			val v = c.getValue();
			if(v != null) cv.put(c.getColumn().getColumnName(), v);
		}
		update(helper, context, tableName, cv, whereColumns, errorHandler);
	}

	/**
	 * UPDATE
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param cv UPDATEする値
	 * @param whereColumns WHEREの条件とするカラムと値のペア
	 * @param errorHandler エラー時処理
	 */
	public static void update(SQLiteOpenHelper helper, Context context, final String tableName
		, final ContentValues cv, final ColumnValuePair[] whereColumns
		, ErrorHandler errorHandler) {
		transaction(helper, context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase db) {

				val linq = whereColumns.linq();

				val q = linq.select(new Func1<ColumnValuePair, String>() {
					@Override
					public String call(ColumnValuePair x) {
						return String.format("%s = ?", x.getColumn().getColumnName());
					}
				}).toJoinedString(" AND ");

				String[] v = linq.select(new Func1<ColumnValuePair, String>() {
					@Override
					public String call(ColumnValuePair x) {
						return x.getValue();
					}
				}).toArray(new String[0]);

				db.update(tableName, cv, q, v);
			}
		}, errorHandler);
	}

	/**
	 * DELETE
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param whereColumns WHEREの条件とするカラムと値のペア
	 */
	public static void delete(SQLiteOpenHelper helper, Context context, String tableName
		, ColumnValuePair... whereColumns) {
		delete(helper, context, tableName, whereColumns, null);
	}

	/**
	 * DELETE
	 * @param helper
	 * @param context
	 * @param tableName テーブル名
	 * @param whereColumns WHEREの条件とするカラムと値のペア
	 * @param errorHandler エラー時処理
	 */
	public static void delete(SQLiteOpenHelper helper, Context context, final String tableName
		, final ColumnValuePair[] whereColumns, ErrorHandler errorHandler) {
		transaction(helper, context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase db) {
				val linq = whereColumns.linq();

				val q = linq.select(new Func1<ColumnValuePair, String>() {
					@Override
					public String call(ColumnValuePair x) {
						return String.format("%s = ?", x.getColumn().getColumnName());
					}
				}).toJoinedString(" AND ");

				String[] v = linq.select(new Func1<ColumnValuePair, String>() {
					@Override
					public String call(ColumnValuePair x) {
						return x.getValue();
					}
				}).toArray(new String[0]);

				db.delete(tableName, q, v);
			}
		}, errorHandler);
	}

	public static String getTableName(Class<? extends ISqlite> clazz) {
		return clazz.getAnnotation(SqliteTable.class).value();
	}

	public static String getDropTableQuery(Class<? extends ISqlite> clazz) {
		return "DROP TABLE " + getTableName(clazz) + ";";
	}

	public static String getCreateTableQuery(Class<? extends ISqlite> clazz) {
		final StringBuilder tblBuilder = new StringBuilder();
		final StringBuilder idxBuilder = new StringBuilder();
		final List<String> primaryKeyList = new ArrayList<String>();
		final Map<String, ArrayList<String>> indexMap = new HashMap<String, ArrayList<String>>();

		tblBuilder.append("CREATE TABLE IF NOT EXISTS ");

		//テーブル名
		SqliteTable tblAttribute = clazz.getAnnotation(SqliteTable.class);

		final String tblName = tblAttribute.value();
		tblBuilder.append(tblName).append(" (");

		//_idのプライマリキー有無
		if(tblAttribute.hasPrimaryId()) tblBuilder.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");

		//カラム名
		clazz.getDeclaredFields().linq().where(new Predicate<Field>() {
			@Override
			public Boolean call(Field x) {
				//SqliteFieldのあるフィールドだけをフィルタする
				return x.getAnnotation(SqliteField.class) != null;
			}
		}).forEach(new Action1<Field>() {
			@Override
			public void call(Field x) {

				//SqliteFieldを取得する
				SqliteField fieldAttribute = x.getAnnotation(SqliteField.class);

				//カラム名
				String columnName = fieldAttribute.name();

				//カラム名をセット
				tblBuilder.append(columnName).append(" ");

				//データ型をセット
				switch(fieldAttribute.type()){
					case ISqlite.FIELD_TEXT:
						tblBuilder.append("TEXT");
						break;
					case ISqlite.FIELD_INTEGER:
						tblBuilder.append("INTEGER");
						break;
					case ISqlite.FIELD_REAL:
						tblBuilder.append("REAL");
						break;
					case ISqlite.FIELD_BLOB:
						tblBuilder.append("BLOB");
						break;
					case ISqlite.FIELD_NULL:
						tblBuilder.append("NULL");
						break;
				}

				//プライマリキー
				if(fieldAttribute.primary()) primaryKeyList.add(columnName);

				//not null有無
				if(fieldAttribute.notNull()) tblBuilder.append(" NOT NULL");

				//一意制約
				if(fieldAttribute.unique()) tblBuilder.append(" UNIQUE");

				//デフォルト値
				if(!"".equals(fieldAttribute.defaultValue())) {
					tblBuilder.append(" DEFAULT ");
					//TEXTの場合はシングルクォーテーションを付与する
					if(fieldAttribute.type() == ISqlite.FIELD_TEXT) {
						tblBuilder.append("'").append(fieldAttribute.defaultValue()).append("'");
					} else {
						tblBuilder.append(fieldAttribute.defaultValue());
					}
				}

				tblBuilder.append(",");

				//Index
				if(!"".equals(fieldAttribute.indexName())) {
					if(!indexMap.containsKey(fieldAttribute.indexName()))
						indexMap.put(fieldAttribute.indexName(), new ArrayList<String>());

					indexMap.get(fieldAttribute.indexName()).add(columnName);

					idxBuilder.append("CREATE INDEX IF NOT EXISTS ")
								.append(fieldAttribute.indexName())
								.append(" ON ").append(tblName)
								.append("(").append(columnName).append(");");
				}
			}
		});

		//最後についた,を削除する
		tblBuilder.deleteCharAt(tblBuilder.length() - 1);

		//PrimaryKeyがある場合は追加する
		if(!primaryKeyList.isEmpty()) {
			tblBuilder.append(" ,PRIMARY KEY(");
			for (String primaryKey : primaryKeyList) {
				tblBuilder.append(primaryKey).append(",");
			}
			tblBuilder.deleteCharAt(tblBuilder.length() - 1);
			tblBuilder.append(")");
		}

		tblBuilder.append(");");

		//Indexがある場合は追加する
		if(!indexMap.isEmpty()) {
			for (Entry<String, ArrayList<String>> index : indexMap.entrySet()) {
				tblBuilder.append("CREATE INDEX IF NOT EXISTS ")
						  .append(index.getKey())
						  .append(" ON ").append(tblName)
						  .append("(");

				for (String indexColumn : index.getValue()) {
					tblBuilder.append(indexColumn).append(",");
				}

				tblBuilder.deleteCharAt(tblBuilder.length() - 1);
				tblBuilder.append(");");
			}
		}

		return tblBuilder.toString();
	}

	public static String getDropCreateQuery(Class<? extends ISqlite> clazz) {
		return getDropTableQuery(clazz) + getCreateTableQuery(clazz);
	}

	private static boolean isMainThread(Context context) {
		return Thread.currentThread().equals(context.getMainLooper().getThread());
	}
}
