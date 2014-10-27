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

@ExtensionMethod({SqliteUtil.class, CursorExtensions.class, Linq.class})
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

	public static List<SpellCard> getAllSpellCards(Context context) {
		val q = new QueryBuilder().selectAll().from(MetaSpellCard.TBL_NAME).toString();
		return new DatabaseHelper(context).getList(q, context, _converter);
	}

	public static List<SpellCard> getHaveSpellCards(Context context) {
		val q = new QueryBuilder().selectAll().from(MetaSpellCard.TBL_NAME)
					.where().equal(MetaSpellCard.GetFlag, true)
					.toString();

		return new DatabaseHelper(context).getList(q, context, _converter);
	}

	public static List<SpellCard> getEquippedSpellCards(Context context) {
		val q = new QueryBuilder().selectAll().from(MetaSpellCard.TBL_NAME)
					.where().equal(MetaSpellCard.EquipmentFlag, true)
					.toString();

		return new DatabaseHelper(context).getList(q, context, _converter);
	}

	public static int getEquipCount(Context context) {
		val q = new QueryBuilder().select(MetaSpellCard.Id).from(MetaSpellCard.TBL_NAME)
					.where().equal(MetaSpellCard.EquipmentFlag, true)
					.toString();

		return new DatabaseHelper(context).getList(q, context, _converter).size();
	}

	public static SpellCard getRandomSpellCard(Context context) {
		val helper = new DatabaseHelper(context);

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


		val getId = new Random().nextInt(maxId) + 1;
		val sql = new QueryBuilder()
						.selectAll()
						.from(MetaSpellCard.TBL_NAME)
						.where().equal(MetaSpellCard.Id, getId)
						.toString();

		val spell =  helper.get(sql, context, _converter);

		helper.transaction(context, new Action1<SQLiteDatabase>() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void call(SQLiteDatabase db) {
				ContentValues cv = new ContentValues();
				cv.put(MetaSpellCard.Count.getColumnName(), (spell.getCount() + 1));
				cv.put(MetaSpellCard.GetFlag.getColumnName(), 1);
				db.update(MetaSpellCard.TBL_NAME, cv, "Id = ?", new String[] { String.valueOf(getId) });

				cv = new ContentValues();
				cv.put(MetaSpellCardHistory.Id.getColumnName(), getId);
				cv.put(MetaSpellCardHistory.Name.getColumnName(), spell.getName());
				SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				cv.put(MetaSpellCardHistory.Timestamp.getColumnName(), df.format(new Date()));
				db.insert(MetaSpellCardHistory.TBL_NAME, null, cv);
			}
		});

		return spell;
	}

	public static void update(Context context, SpellCard spellCard) {

		val cv = new ContentValues();
		val id = spellCard.getId();

		cv.put(MetaSpellCard.Count.getColumnName(), spellCard.getCount() + 1);
		cv.put(MetaSpellCard.GetFlag.getColumnName(), 1);

		new DatabaseHelper(context).transaction(context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase x) {
				x.update(MetaSpellCard.TBL_NAME, cv, "Id = ?", new String[]{String.valueOf(id)});
			}
		});
	}

	public static void insert(Context context, SpellCard spellCard) {
		val q = createInsertQuery(spellCard);

		new DatabaseHelper(context).transaction(context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase x) {
				x.execSQL(q);
			}
		});
	}

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


	private static HashMap<String, Map<Long, String>> _names;
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