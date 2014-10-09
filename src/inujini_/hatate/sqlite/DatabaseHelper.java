package inujini_.hatate.sqlite;

import inujini_.hatate.data.TwitterAccount;
import inujini_.sqlite.helper.SqliteUtil;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@ExtensionMethod({SqliteUtil.class})
public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "HATATE_DB";

	public static final String KEY_OPEN_DB = "OpenDbPreference";
	public static final String KEY_IS_OPENED = "isOpened";

	private Context _context;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		_context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.beginTransaction();

		try {
			db.execSQL(TwitterAccount.class.getCreateTableQuery());
			db.setTransactionSuccessful();

			val pref = _context.getSharedPreferences(KEY_OPEN_DB, 0);
			if(!pref.getBoolean(KEY_IS_OPENED, false)) {
				pref.edit().putBoolean(KEY_IS_OPENED, true).commit();
			}
		} finally {
			db.endTransaction();
			_context = null;
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public static boolean isDbOpened(Context context) {
		val pref = context.getSharedPreferences(KEY_OPEN_DB, 0);
		return pref.getBoolean(KEY_IS_OPENED, false);
	}

}
