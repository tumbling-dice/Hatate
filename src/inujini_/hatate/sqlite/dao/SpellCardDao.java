/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.sqlite.dao;

import inujini_.hatate.data.SpellCard;
import inujini_.hatate.data.SpellCardHistory;
import inujini_.hatate.data.meta.MetaCharacter;
import inujini_.hatate.data.meta.MetaSpellCard;
import inujini_.hatate.data.meta.MetaSpellCardHistory;
import inujini_.hatate.function.Function.Action1;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.linq.Linq;
import inujini_.hatate.sqlite.DatabaseHelper;
import inujini_.hatate.sqlite.helper.ColumnValuePair;
import inujini_.hatate.sqlite.helper.ContentValuesExtensions;
import inujini_.hatate.sqlite.helper.CursorExtensions;
import inujini_.hatate.sqlite.helper.QueryBuilder;
import inujini_.hatate.sqlite.helper.SqliteUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * {@link SpellCard}のDAO.
 */
@ExtensionMethod({SqliteUtil.class, CursorExtensions.class, Linq.class, ContentValuesExtensions.class})
public class SpellCardDao {

	private static final Func1<Cursor, SpellCard> _converter = new Func1<Cursor, SpellCard>() {
		@Override
		public SpellCard call(Cursor x) {
			val s = new SpellCard();
			s.setId(x.getLongByMeta(MetaSpellCard.Id));
			s.setName(x.getStringByMeta(MetaSpellCard.Name));
			s.setPower(x.getIntByMeta(MetaSpellCard.Power));
			s.setCount(x.getIntByMeta(MetaSpellCard.Count));
			s.setGot(x.getBooleanByMeta(MetaSpellCard.GetFlag));
			s.setEquipped(x.getBooleanByMeta(MetaSpellCard.EquipmentFlag));
			s.setCharacterId(x.getIntByMeta(MetaSpellCard.CharacterId));
			val seriesIds = x.getStringByMeta(MetaSpellCard.SeriesId);
			s.setSeriesId(seriesIds.split(",").linq().select(new Func1<String, Integer>() {
				@Override
				public Integer call(String y) {
					return Integer.parseInt(y);
				}
			}).toIntArray());
			return s;
		}
	};

	/**
	 * 全スペルカードの取得.
	 * @param context
	 * @return DBに登録されている全ての{@link SpellCard}.
	 */
	public static List<SpellCard> getAllSpellCards(Context context) {
		val q = new QueryBuilder().selectAll().from(MetaSpellCard.TBL_NAME).toString();
		return new DatabaseHelper(context).getList(q, context, _converter);
	}

	/**
	 * 所持スペルカードの取得.
	 * @param context
	 * @return 所持済みの{@link SpellCard}.
	 * @see SpellCard#isGot()
	 */
	public static List<SpellCard> getHaveSpellCards(Context context) {
		val q = new QueryBuilder().selectAll().from(MetaSpellCard.TBL_NAME)
					.where().equal(MetaSpellCard.GetFlag, true)
					.toString();

		return new DatabaseHelper(context).getList(q, context, _converter);
	}

	/**
	 * 装備済みスペルカードの取得.
	 * @param context
	 * @return 装備済みの{@link SpellCard}.
	 * @see SpellCard#isEquipped()
	 */
	public static List<SpellCard> getEquippedSpellCards(Context context) {
		val q = new QueryBuilder().selectAll().from(MetaSpellCard.TBL_NAME)
					.where().equal(MetaSpellCard.EquipmentFlag, true)
					.toString();

		return new DatabaseHelper(context).getList(q, context, _converter);
	}

	/**
	 * 装備済みスペルカード数取得.
	 * @param context
	 * @return 装備済みのスペルカードの総数.
	 */
	public static int getEquipCount(Context context) {
		val q = new QueryBuilder().select(MetaSpellCard.Id).from(MetaSpellCard.TBL_NAME)
					.where().equal(MetaSpellCard.EquipmentFlag, true)
					.toString();

		return new DatabaseHelper(context).getList(q, context, _converter).size();
	}

	/**
	 * <p>スペルカードランダム取得.</p>
	 * <p>スペルカード1枚をランダムに取得し、それを返す.</p>
	 * <p>取得枚数のカウントアップ及び取得履歴情報の書き込みはこのメソッド内で行われる.</p>
	 * @param context
	 * @return ランダムに取得した{@link SpellCard}.
	 */
	public static SpellCard getRandomSpellCard(Context context) {
		val helper = new DatabaseHelper(context);

		// 一番大きいスペルカードIDを取得する
		val q = new QueryBuilder()
						.select(MetaSpellCard.Id)
						.from(MetaSpellCard.TBL_NAME)
						.orderByDesc(MetaSpellCard.Id)
						.limit(1)
						.toString();

		val maxId = helper.get(q, context, new Func1<Cursor, Integer>() {
			@Override
			public Integer call(Cursor c) {
				return c.getIntByMeta(MetaSpellCard.Id);
			}
		});

		// 一番大きいスペルカードIDを基にランダムなIDを生成
		val getId = new Random().nextInt(maxId) + 1;
		val sql = new QueryBuilder()
						.selectAll()
						.from(MetaSpellCard.TBL_NAME)
						.where().equal(MetaSpellCard.Id, getId)
						.toString();

		val spell =  helper.get(sql, context, _converter);

		// 取得したスペルカード情報の登録
		helper.transaction(context, new Action1<SQLiteDatabase>() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void call(SQLiteDatabase db) {
				// カウントアップ
				ContentValues cv = new ContentValues()
									.putInt(MetaSpellCard.Count, (spell.getCount() + 1))
									.putBoolean(MetaSpellCard.GetFlag, true);
				db.update(MetaSpellCard.TBL_NAME, cv, "Id = ?", new String[] { String.valueOf(getId) });

				// 履歴情報の登録
				cv = new ContentValues()
						.putInt(MetaSpellCardHistory.Id, getId)
						.putString(MetaSpellCardHistory.Name, spell.getName())
						.putString(MetaSpellCardHistory.Timestamp
								, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
				db.insert(MetaSpellCardHistory.TBL_NAME, null, cv);
			}
		});

		return spell;
	}

	/**
	 * スペルカード情報更新.
	 * @param context
	 * @param spellCard 更新対象のスペルカード
	 */
	public static void update(Context context, SpellCard spellCard) {

		val id = spellCard.getId();

		val cv = new ContentValues()
				.putInt(MetaSpellCard.Count, spellCard.getCount() + 1)
				.putBoolean(MetaSpellCard.GetFlag, true);

		new DatabaseHelper(context).transaction(context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase x) {
				x.update(MetaSpellCard.TBL_NAME, cv, "Id = ?", new String[]{String.valueOf(id)});
			}
		});
	}

	/**
	 * スペルカード情報挿入.
	 * @param context
	 * @param spellCard 挿入するスペルカード
	 */
	public static void insert(Context context, SpellCard spellCard) {
		val q = createInsertQuery(spellCard);

		new DatabaseHelper(context).transaction(context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase x) {
				x.execSQL(q);
			}
		});
	}

	/**
	 * スペルカード情報一括挿入.
	 * @param context
	 * @param spellCards 挿入するスペルカード
	 */
	public static void bulkInsert(Context context, List<SpellCard> spellCards) {
		val querys = spellCards.linq().select(new Func1<SpellCard, String>(){
			@Override
			public String call(SpellCard s) {
				return createInsertQuery(s);
			}
		}).toList();

		new DatabaseHelper(context).transaction(context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase x) {
				for(val q : querys)
					x.execSQL(q);
			}
		});
	}

	private static String createInsertQuery(SpellCard s) {

		return new QueryBuilder()
				.insert(MetaSpellCard.TBL_NAME
					, new ColumnValuePair(MetaSpellCard.Name, s.getName())
					, new ColumnValuePair(MetaSpellCard.Power, s.getPower())
					, new ColumnValuePair(MetaSpellCard.Count, s.getCount())
					, new ColumnValuePair(MetaSpellCard.GetFlag, s.isGot())
					, new ColumnValuePair(MetaSpellCard.EquipmentFlag, s.isEquipped())
					, new ColumnValuePair(MetaSpellCard.CharacterId, s.getCharacterId())
					, new ColumnValuePair(MetaSpellCard.SeriesId
							, s.getSeriesId().linq().select(new Func1<Integer, String>() {
								@Override
								public String call(Integer x) {
									return String.valueOf(x);
								}
							}).toJoinedString(","))
				)
				.toString();
	}

	// FIX ME:これはCharacterDaoとSeriesDaoにあるべきだと思う…
	private static HashMap<String, Map<Long, String>> _names;
	/**
	 *
	 * @param context
	 * @param id
	 * @param tableName
	 * @return
	 */
	public static String getName(Context context, long id, String tableName) {
		if(_names == null){
			_names = new HashMap<String, Map<Long, String>>();
		}

		if(!_names.containsKey(tableName)) {
			val q = new QueryBuilder()
					.select(MetaCharacter.Id, MetaCharacter.Name)
					.from(tableName)
					.where().equal(MetaCharacter.Id, id)
					.toString();


			_names.put(tableName, new DatabaseHelper(context).getMap(q, context, new Func1<Cursor, Long>(){
				@Override
				public Long call(Cursor x) {
					return x.getLong(x.getColumnIndex("Id"));
				}
			}, new Func1<Cursor, String>() {
				@Override
				public String call(Cursor x) {
					return x.getString(x.getColumnIndex("Name"));
				}
			}));
		}

		return _names.get(tableName).get(id);
	}

	/**
	 * スペルカード履歴取得.
	 * @param context
	 * @param limit 取得する数
	 * @return limitで指定された数だけの最新{@link SpellCardHistory}.
	 */
	public static List<SpellCardHistory> getHistory(final Context context, int limit) {
		val q = new QueryBuilder()
					.selectAll()
					.from(MetaSpellCardHistory.TBL_NAME)
					.orderByDesc(MetaSpellCardHistory.HistoryOrder)
					.limit(limit)
					.toString();

		return new DatabaseHelper(context).getList(q, context, new Func1<Cursor, SpellCardHistory>(){
			@Override
			public SpellCardHistory call(Cursor x) {
				val history = new SpellCardHistory();
				val id = x.getLongByMeta(MetaSpellCardHistory.Id);
				history.setOrder(x.getIntByMeta(MetaSpellCardHistory.HistoryOrder));
				history.setId(id);
				history.setName(x.getStringByMeta(MetaSpellCardHistory.Name));
				history.setTimestamp(x.getStringByMeta(MetaSpellCardHistory.Timestamp));

				val joinQuery = new QueryBuilder()
									.select(MetaSpellCard.CharacterId)
									.from(MetaSpellCard.TBL_NAME)
									.where().equal(MetaSpellCard.Id, id)
									.toString();

				history.setCharacterId(new DatabaseHelper(context).get(joinQuery, context, new Func1<Cursor, Integer>(){
					@Override
					public Integer call(Cursor y) {
						return y.getIntByMeta(MetaSpellCard.CharacterId);
					}
				}));

				return history;
			}
		});
	}
}
