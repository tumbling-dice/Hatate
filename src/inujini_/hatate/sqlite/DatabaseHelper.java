/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.sqlite;

import inujini_.hatate.data.Character;
import inujini_.hatate.data.Series;
import inujini_.hatate.data.SpellCard;
import inujini_.hatate.data.SpellCardHistory;
import inujini_.hatate.data.Statistics;
import inujini_.hatate.data.TwitterAccount;
import inujini_.hatate.data.meta.MetaCharacter;
import inujini_.hatate.data.meta.MetaSeries;
import inujini_.hatate.data.meta.MetaSpellCard;
import inujini_.hatate.function.Function.Action1;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.linq.Linq;
import inujini_.hatate.scraping.Scraper.XElement;
import inujini_.hatate.scraping.XmlScraper;
import inujini_.hatate.service.Houtyou;
import inujini_.hatate.sqlite.dao.StatisticsDao;
import inujini_.hatate.sqlite.helper.CursorExtensions;
import inujini_.hatate.sqlite.helper.QueryBuilder;
import inujini_.hatate.sqlite.helper.SqliteUtil;
import inujini_.hatate.util.Util;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import lombok.val;
import lombok.experimental.ExtensionMethod;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 */
@ExtensionMethod({SqliteUtil.class, Linq.class, CursorExtensions.class})
public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DB_VERSION = 4;
	private static final String DB_NAME = "HATATE_DB";

	public static final String KEY_DB_PREFERENCE = "OpenDbPreference";
	public static final String KEY_IS_OPENED = "isOpened";
	public static final String KEY_CURRENT_DB_VERSION = "currentDBVersion";

	private WeakReference<Context> _context;

	/**
	 *
	 * @param context
	 */
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		_context = new WeakReference<Context>(context);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.beginTransaction();
		val context = _context.get();

		try {
			db.execSQL(TwitterAccount.class.getCreateTableQuery());

			updateTo2(db, context);
			updateTo3(db, context);
			db.setTransactionSuccessful();

			val editor = context.getSharedPreferences(KEY_DB_PREFERENCE, 0).edit();
			editor.putBoolean(KEY_IS_OPENED, true);
			editor.putInt(KEY_CURRENT_DB_VERSION, DB_VERSION).commit();
		} finally {
			db.endTransaction();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.beginTransaction();
		val context = _context.get();
		try {
			// to version 2
			if(oldVersion <= 1 && newVersion >= 2) {
				updateTo2(db, context);
			}

			// to version 3
			if(oldVersion <= 2 && newVersion >= 3) {
				updateTo3(db, context);
			}

			if(oldVersion <= 3 && newVersion >= 4) {
				updateTo4(db, context);
			}

			val pref = context.getSharedPreferences(KEY_DB_PREFERENCE, 0);
			if(pref.getInt(KEY_CURRENT_DB_VERSION, oldVersion) != newVersion) {
				pref.edit().putInt(KEY_CURRENT_DB_VERSION, newVersion).commit();
			}

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}

	/**
	 * [2014/10/13]
	 * db version 2
	 * 1.create Statistics Table
	 * 2.init Statistics
	 * @param db
	 * @param context
	 */
	private static void updateTo2(SQLiteDatabase db, Context context) {
		db.execSQL(Statistics.class.getCreateTableQuery());
		val killCount = context.getSharedPreferences(Houtyou.KEY_KILL, 0).getInt(Houtyou.KEY_KILL_COUNT, 0);
		StatisticsDao.init(db, killCount);
	}

	/**
	 * [2014/10/18]
	 * db version 3
	 * 1. add SpellCard Rule
	 * @param db
	 * @param context
	 */
	private static void updateTo3(final SQLiteDatabase db, Context context) {
		db.execSQL(Series.class.getCreateTableQuery());
		db.execSQL(Character.class.getCreateTableQuery());
		db.execSQL(SpellCard.class.getCreateTableQuery());
		db.execSQL(SpellCardHistory.class.getCreateTableQuery());

		XmlScraper scraper = null;
		try {
			scraper = new XmlScraper(Util.getAssetStream(context, "series.xml"));
			scraper.extract("Seriese").linq().forEach(new Action1<XElement>() {
				@Override
				public void call(XElement x) {
					val cv = new ContentValues();
					cv.put(MetaSeries.Id.getColumnName(), Integer.parseInt(x.getAttributeValue("id")));
					cv.put(MetaSeries.Name.getColumnName(), x.getText());
					db.insert(MetaSeries.TBL_NAME, null, cv);
				}
			});

			scraper.close();

			scraper = new XmlScraper(Util.getAssetStream(context, "characters.xml"));
			scraper.extract("Character").linq().forEach(new Action1<XElement>() {
				@Override
				public void call(XElement x) {
					val cId = Integer.parseInt(x.getAttributeValue("id"));
					val cv = new ContentValues();
					cv.put(MetaCharacter.Id.getColumnName(), cId);
					cv.put(MetaCharacter.Name.getColumnName(), x.getAttributeValue("name"));
					db.insert(MetaCharacter.TBL_NAME, null, cv);

					x.getInnerElements().linq().selectMany(new Func1<XElement, Iterable<XElement>>() {
						@Override
						public Iterable<XElement> call(XElement y) {
							return y.getInnerElements();
						}
					}).forEach(new Action1<XElement>() {
						@Override
						public void call(XElement y) {
							val cvs = new ContentValues();
							cvs.put(MetaSpellCard.Id.getColumnName(), Integer.parseInt(y.getAttributeValue("id")));
							cvs.put(MetaSpellCard.Name.getColumnName(), y.getAttributeValue("name"));
							cvs.put(MetaSpellCard.Power.getColumnName(), Integer.parseInt(y.getAttributeValue("power")));
							cvs.put(MetaSpellCard.CharacterId.getColumnName(), cId);
							cvs.put(MetaSpellCard.SeriesId.getColumnName()
									, y.getInnerElements().linq().select(new Func1<XElement, String>() {
										@Override
										public String call(XElement z) {
											return z.getText();
										}
									}).toJoinedString(","));

							db.insert(MetaSpellCard.TBL_NAME, null, cvs);
						}
					});
				}
			});

			scraper.close();

		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if(scraper != null) {
				try {
					scraper.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * [2014/10/26]
	 * db version 4
	 * 1. having spellcard's flag on
	 * (update only)
	 * @param db
	 * @param context
	 */
	private static void updateTo4(final SQLiteDatabase db, Context context) {
		val q = new QueryBuilder()
					.select(MetaSpellCard.Id)
					.from(MetaSpellCard.TBL_NAME)
					.where().bigger(MetaSpellCard.Count, 0)
					.toString();

		val c = db.rawQuery(q, null);

		if(!c.moveToFirst()) {
			c.close();
			return;
		}

		val ids = new ArrayList<Long>();
		try {
			do {
				ids.add(c.getLongByMeta(MetaSpellCard.Id));
			} while (c.moveToNext());
		} finally {
			c.close();
		}

		ids.linq().forEach(new Action1<Long>() {
			@Override
			public void call(Long x) {
				val cv = new ContentValues();
				cv.put(MetaSpellCard.GetFlag.getColumnName(), true);
				db.update(MetaSpellCard.TBL_NAME, cv, "Id = ?", new String[] { x.toString() });
			}
		});
	}

	/**
	 * DB初期化チェック.
	 * @param context
	 * @return DBが既に初期化済みであればtrue
	 */
	public static boolean isDbOpened(Context context) {
		val pref = context.getSharedPreferences(KEY_DB_PREFERENCE, 0);
		return pref.getBoolean(KEY_IS_OPENED, false);
	}

	/**
	 * DB更新済みチェック.
	 * @param context
	 * @return DBが既に最新のバージョンであればtrue
	 */
	public static boolean isDbUpdated(Context context) {
		val pref = context.getSharedPreferences(KEY_DB_PREFERENCE, 0);
		return pref.getInt(KEY_CURRENT_DB_VERSION, 0) == DB_VERSION;
	}

}
