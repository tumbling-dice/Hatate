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
import lombok.val;
import lombok.experimental.ExtensionMethod;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

@ExtensionMethod({SqliteUtil.class, CursorExtensions.class})
public class AccountDao {

	public static Twitter getTwitter(Context context) {
		val q = new QueryBuilder();
		val query = q.selectAll()
						.from(MetaAccount.TBL_NAME)
						.toString();

		val res = context.getResources();
		val consumerKey = res.getString(R.string.consumer_key);
		val consumerSecret = res.getString(R.string.consumer_secret);

		return new DatabaseHelper(context).get(query, context, new Func1<Cursor, Twitter>(){
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

	public static boolean isAuthorized(Context context) {
		if(!DatabaseHelper.isDbOpened(context)) return false;
		return getTwitter(context) != null;
	}

}
