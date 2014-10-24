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
import inujini_.hatate.R;
import inujini_.hatate.data.TwitterAccount;
import inujini_.hatate.data.meta.MetaAccount;
import inujini_.hatate.sqlite.DatabaseHelper;
import inujini_.sqlite.helper.CursorExtensions;
import inujini_.sqlite.helper.QueryBuilder;
import inujini_.sqlite.helper.SqliteUtil;

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
 * TwitterAccount��DAO
 */
@ExtensionMethod({SqliteUtil.class, CursorExtensions.class})
public class AccountDao {

	/** Cursor -> TwitterAccount (selectAll��p) */
	private static final Func1<Cursor, TwitterAccount> _converter = new Func1<Cursor, TwitterAccount>() {
		@Override
		public TwitterAccount call(Cursor c) {
			val a = new TwitterAccount();
			a.setScreenName(c.getStringByMeta(MetaAccount.ScreenName));
			a.setUserId(Long.parseLong(c.getStringByMeta(MetaAccount.UserId)));
			a.setUse(c.getBooleanByMeta(MetaAccount.UseFlag));
			return a;
		}
	};

	/**
	 * �A�g�pTwitter�擾.
	 * @param context
	 * @return �g�p�t���O�������Ă��邷�ׂẴA�J�E���g��{@link Twitter}
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
	 * �S�A�J�E���g�擾.
	 * @param context
	 * @return �S{@link TwitterAccount}�̃��X�g
	 */
	public static List<TwitterAccount> getAllAccount(Context context) {
		val q = new QueryBuilder()
					.selectAll()
					.from(MetaAccount.TBL_NAME)
					.toString();

		return new DatabaseHelper(context).getList(q, context, _converter);
	}

	/**
	 * �f�[�^�o�^.
	 * @param account �o�^����A�J�E���g
	 * @param context
	 */
	public static void insert(TwitterAccount account, Context context) {
		val values = new ContentValues();
		values.put(MetaAccount.ScreenName.getColumnName(), account.getScreenName());
		values.put(MetaAccount.UserId.getColumnName(), account.getUserId());
		values.put(MetaAccount.AccessToken.getColumnName(), account.getAccessToken());
		values.put(MetaAccount.AccessSecret.getColumnName(), account.getAccessSecret());

		new DatabaseHelper(context).transaction(context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase db) {
				db.insert(MetaAccount.TBL_NAME, null, values);
			}
		});
	}

	/**
	 * �f�[�^�o�^(�񓯊�).
	 * @param account �o�^����A�J�E���g
	 * @param context
	 */
	public static void insertAsync(final TwitterAccount account, Context context) {
		new ReactiveAsyncTask<Context, Void, Void>(new Func1<Context, Void>(){
			@Override
			public Void call(Context x) {
				AccountDao.insert(account, x);
			}
		}).execute(context);
	}

	/**
	 * �g�p�t���O�X�V.
	 * @param userId Twitter��userId
	 * @param isUse �g�p�t���O
	 * @param context
	 */
	public static void setUseFlag(long userId, boolean isUse, Context context) {
		val cv = new ContentValues();
		cv.put(MetaAccount.UseFlag.getColumnName(), (isUse ? 1: 0));

		new DatabaseHelper(context).transaction(context, new Action1<SQLiteDatabase>() {
			@Override
			public void call(SQLiteDatabase db) {
				db.update(MetaAccount.TBL_NAME, cv, "UserId = ?", new String[]{ String.valueOf(userId) });
			}
		});
	}

	/**
	 * �g�p�t���O�X�V.
	 * @param account
	 * @param context
	 */
	public static void setUseFlag(TwitterAccount account, Context context) {
		setUseFlag(account.getUserId(), account.isUse(), context);
	}

	/**
	 * �g�p�t���O�X�V(�񓯊���).
	 * @param account
	 * @param context
	 */
	public static void setUseFlagAsync(final TwitterAccount account, Context context) {
		new ReactiveAsyncTask<Context, Void, Void>(new Func1<Context, Void>(){
			@Override
			public Void call(Context x) {
				AccountDao.update(account, x);
			}
		}).execute(context);
	}

	/**
	 * �A�J�E���g�폜.
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
	 * �A�J�E���g�폜(�񓯊���).
	 * @param account
	 * @param context
	 */
	public static void deleteAsync(final TwitterAccount account, Context context) {
		new ReactiveAsyncTask<Context, Void, Void>(new Func1<Context, Void>(){
			@Override
			public Void call(Context x) {
				AccountDao.delete(account, x);
			}
		}).execute(context);
	}

	/**
	 * �F�؍ς݃f�[�^���݃`�F�b�N.
	 * @param context
	 * @return ����1���ł��o�^����Ă�����true
	 */
	public static boolean isAuthorized(Context context) {
		// FIXME: ��������������
		// �����̃J�E���g���Ƃ�ׂ��B
		val t = getTwitter(context);
		return t != null && !t.isEmpty();
	}
	
	/**
	 * �o�^�ς݃`�F�b�N.
	 * @param userId �`�F�b�N�������A�J�E���g��user_id
	 * @param context
	 * @return userId�������R�[�h�����݂���ꍇ��true
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
