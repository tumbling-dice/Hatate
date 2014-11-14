/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.sqlite.dao;

import inujini_.hatate.R;
import inujini_.hatate.data.TwitterAccount;
import inujini_.hatate.data.meta.MetaAccount;
import inujini_.hatate.function.Function.Action1;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import inujini_.hatate.sqlite.DatabaseHelper;
import inujini_.hatate.sqlite.helper.CursorExtensions;
import inujini_.hatate.sqlite.helper.QueryBuilder;
import inujini_.hatate.sqlite.helper.SqliteUtil;

import java.util.List;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * {@link TwitterAccount}のDAO
 */
@ExtensionMethod({SqliteUtil.class, CursorExtensions.class, ContentValuesExtensions.class})
public class AccountDao {

	/**
	 * 連携用Twitter取得.
	 * @param context
	 * @return 使用フラグが立っているすべてのアカウントの{@link Twitter}
	 */
	public static List<Twitter> getTwitter(Context context) {
		val q = new QueryBuilder()
					.selectAll()
					.from(MetaAccount.TBL_NAME)
					.where().equal(MetaAccount.UseFlag, true)
					.toString();

		val res = context.getResources();
		val consumerKey = res.getString(R.string.consumer_key);
		val consumerSecret = res.getString(R.string.consumer_secret);

		return new DatabaseHelper(context).getList(q, context, new Func1<Cursor, Twitter>(){
			@Override
			public Twitter call(Cursor c) {
				return new TwitterFactory(
						new ConfigurationBuilder()
						.setOAuthConsumerKey(consumerKey)
						.setOAuthConsumerSecret(consumerSecret)
						.setOAuthAccessToken(c.getStringByMeta(MetaAccount.AccessToken))
						.setOAuthAccessTokenSecret(c.getStringByMeta(MetaAccount.AccessSecret))
						.setHttpConnectionTimeout(15000)
						.setHttpReadTimeout(30000)
						.build()
				).getInstance();
			}
		});
	}

	/**
	 * 登録済みデータ件数取得
	 * @param context
	 * @return 登録済みデータの件数
	 */
	public static int getCount(Context context) {
		val q = new QueryBuilder()
					.select(MetaAccount.Id)
					.from(MetaAccount.TBL_NAME)
					.toString();

		return new DatabaseHelper(context).getList(q, context, new Func1<Cursor, Integer>() {
			@Override
			public Integer call(Cursor arg0) {
				return 0;
			}
		}).size();

	}

	/**
	 * 全アカウント取得.
	 * @param context
	 * @return 全{@link TwitterAccount}のリスト
	 */
	public static List<TwitterAccount> getAllAccount(Context context) {
		val q = new QueryBuilder()
					.selectAll()
					.from(MetaAccount.TBL_NAME)
					.toString();

		return new DatabaseHelper(context).getList(q, context, new Func1<Cursor, TwitterAccount>() {
			@Override
			public TwitterAccount call(Cursor c) {
				val a = new TwitterAccount();
				a.setScreenName(c.getStringByMeta(MetaAccount.ScreenName));
				a.setUserId(Long.parseLong(c.getStringByMeta(MetaAccount.UserId)));
				a.setUse(c.getBooleanByMeta(MetaAccount.UseFlag));
				return a;
			}
		});
	}

	/**
	 * データ登録.
	 * @param account 登録するアカウント
	 * @param context
	 */
	public static void insert(TwitterAccount account, Context context) {
		val values = new ContentValues()
						.putString(MetaAccount.ScreenName, account.getScreenName())
						.putLong(MetaAccount.UserId, account.getUserId())
						.putString(MetaAccount.AccessToken, account.getAccessToken())
						.putString(MetaAccount.AccessSecret, account.getAccessSecret());

		new DatabaseHelper(context).transaction(context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase db) {
				db.insert(MetaAccount.TBL_NAME, null, values);
			}
		});
	}

	/**
	 * データ登録(非同期).
	 * @param account 登録するアカウント
	 * @param context
	 */
	public static void insertAsync(final TwitterAccount account, Context context) {
		new ReactiveAsyncTask<Context, Void, Void>(new Func1<Context, Void>(){
			@Override
			public Void call(Context x) {
				AccountDao.insert(account, x);
				return null;
			}
		}).execute(context);
	}

	/**
	 * 使用フラグ更新.
	 * @param userId TwitterのuserId
	 * @param isUse 使用フラグ
	 * @param context
	 */
	public static void setUseFlag(final long userId, boolean isUse, Context context) {
		val cv = new ContentValues().putBoolean(MetaAccount.UseFlag, isUse);

		new DatabaseHelper(context).transaction(context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase db) {
				db.update(MetaAccount.TBL_NAME, cv, "UserId = ?", new String[]{ String.valueOf(userId) });
			}
		});
	}

	/**
	 * 使用フラグ更新.
	 * @param account
	 * @param context
	 */
	public static void setUseFlag(TwitterAccount account, Context context) {
		setUseFlag(account.getUserId(), account.isUse(), context);
	}

	/**
	 * 使用フラグ更新(非同期版).
	 * @param account
	 * @param context
	 */
	public static void setUseFlagAsync(final TwitterAccount account, Context context) {
		new ReactiveAsyncTask<Context, Void, Void>(new Func1<Context, Void>(){
			@Override
			public Void call(Context x) {
				AccountDao.setUseFlag(account, x);
				return null;
			}
		}).execute(context);
	}

	/**
	 * アカウント削除.
	 * @param account
	 * @param context
	 */
	public static void delete(TwitterAccount account, Context context) {
		val userId = account.getUserId();

		new DatabaseHelper(context).transaction(context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase db) {
				db.delete(MetaAccount.TBL_NAME, "UserId = ?", new String[]{ String.valueOf(userId) });
			}
		});
	}

	/**
	 * アカウント削除(非同期版).
	 * @param account
	 * @param context
	 */
	public static void deleteAsync(final TwitterAccount account, Context context) {
		new ReactiveAsyncTask<Context, Void, Void>(new Func1<Context, Void>(){
			@Override
			public Void call(Context x) {
				AccountDao.delete(account, x);
				return null;
			}
		}).execute(context);
	}

	/**
	 * 認証済みデータ存在チェック.
	 * @param context
	 * @return 既に1件でも登録されていたらtrue
	 */
	public static boolean isAuthorized(Context context) {
		return getCount(context) != 0;
	}

	/**
	 * 登録済みチェック.
	 * @param userId チェックしたいアカウントのuser_id
	 * @param context
	 * @return userIdを持つレコードが存在する場合はtrue
	 */
	public static boolean isExist(long userId, Context context) {
		val q = new QueryBuilder()
				.select(MetaAccount.UserId)
				.from(MetaAccount.TBL_NAME)
				.where().equal(MetaAccount.UserId, userId)
				.toString();

		return new DatabaseHelper(context).get(q, context, new Func1<Cursor, Long>() {
			@Override
			public Long call(Cursor c) {
				return Long.parseLong(c.getStringByMeta(MetaAccount.UserId));
			}
		}) != null;
	}

}
