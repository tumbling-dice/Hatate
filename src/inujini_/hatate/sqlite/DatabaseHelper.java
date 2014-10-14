package inujini_.hatate.sqlite;

import inujini_.hatate.data.Statistics;
import inujini_.hatate.data.TwitterAccount;
import inujini_.hatate.service.Houtyou;
import inujini_.hatate.sqlite.dao.StatisticsDao;
import inujini_.sqlite.helper.SqliteUtil;

import java.lang.ref.WeakReference;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@ExtensionMethod({SqliteUtil.class})
public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DB_VERSION = 2;
	private static final String DB_NAME = "HATATE_DB";

	public static final String KEY_DB_PREFERENCE = "OpenDbPreference";
	public static final String KEY_IS_OPENED = "isOpened";
	public static final String KEY_CURRENT_DB_VERSION = "currentDBVersion";

	private WeakReference<Context> _context;

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
			db.setTransactionSuccessful();

			val pref = context.getSharedPreferences(KEY_DB_PREFERENCE, 0);
			if(!pref.getBoolean(KEY_IS_OPENED, false)) {
				pref.edit().putBoolean(KEY_IS_OPENED, true).commit();
			}
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

	public static boolean isDbOpened(Context context) {
		val pref = context.getSharedPreferences(KEY_DB_PREFERENCE, 0);
		return pref.getBoolean(KEY_IS_OPENED, false);
	}

	public static boolean isDbUpdated(Context context) {
		val pref = context.getSharedPreferences(KEY_DB_PREFERENCE, 0);
		return pref.getInt(KEY_CURRENT_DB_VERSION, 0) == DB_VERSION;
	}

}
