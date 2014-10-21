/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import inujini_.function.Function.Action;
import inujini_.function.Function.Action1;
import inujini_.function.Function.Func1;
import inujini_.hatate.data.TwitterAccount;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import inujini_.hatate.sqlite.dao.AccountDao;
import lombok.val;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * <p>Oauth認証画面.</p>
 * <p>Intentに{@link KEY_NEED_CALLBACK}としてtrueが渡されていた場合、Oauth認証済みの{@link TwitterAccount}を返す.</p>
 * <p>基本的にこのActivityはOauth認証画面を表示し、終わったら{@link Activity#finish()}するだけなので、
 * {@link Intent#FLAG_ACTIVITY_NO_HISTORY}を使って起動すること.</p>
 *
 * @see Activity#startActivityForResult(Intent, int)
 * @see Activity#onActivityResult(int, int, Intent)
 */
public class OauthActivity extends Activity {
	
	/**
	 * Activityのコールバック設定用キー.
	 * 
	 * @see #KEY_ACCOUNT
	 */
	public static final String KEY_NEED_CALLBACK = "isNeedCallback";
	
	/**
	 * 戻り値となる{@link TwitterAccount}の取得用キー.
	 * 
	 * @see #KEY_NEED_CALLBACK
	 */
	public static final Stirng KEY_ACCOUNT = "account";
	
	private OAuthAuthorization _oauth;
	private boolean _isNeedCallback = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		val intent = super.getIntent();
		if(intent != null) _isNeedCallback = intent.getBooleanExtra(KEY_NEED_CALLBACK, false);

		val res = getApplicationContext().getResources();
		val consumerKey = res.getString(R.string.consumer_key);
		val consumerSecret = res.getString(R.string.consumer_secret);
		val callbackUri = res.getString(R.string.callback);

		val conf  = new ConfigurationBuilder()
					.setOAuthConsumerKey(consumerKey)
					.setOAuthConsumerSecret(consumerSecret);

		_oauth = new OAuthAuthorization(conf.build());
		_oauth.setOAuthAccessToken(null);

		val prog = new ProgressDialog(this);
		prog.setTitle("通信中...");
		prog.setMessage("認証用URLに接続しています...");
		prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prog.setCancelable(false);

		new ReactiveAsyncTask<String, Void, String>(new Func1<String, String>() {
			@Override
			public String call(String callback) {
				String uri;
				try {
					uri = _oauth.getOAuthRequestToken(callback).getAuthorizationURL();
				} catch (TwitterException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				
				if(_isNeedCallback) uri += "&force_login=true";

				return uri;
			}
		}).setOnPreExecute(new Action() {
			@Override
			public void call() {
				prog.show();
			}
		}).setOnPostExecute(new Action1<String>() {
			@Override
			public void call(String uri) {
				if(prog != null || prog.isShowing()) {
					prog.dismiss();
				}

				//Oauth認証開始
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
			}
		}).setOnError(new Action1<Exception>() {
			@Override
			public void call(Exception e) {
				if(prog != null || prog.isShowing()) {
					prog.dismiss();
				}
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Twitterとの接続に失敗しました", Toast.LENGTH_LONG).show();
				setResult(RESULT_CANCELED);
				finish();
			}
		}).execute(callbackUri);
	}

	@Override
	protected void onNewIntent(Intent _intent) {
		super.onNewIntent(_intent);
		val uri = _intent.getData();
		val res = getApplicationContext().getResources();

		if (uri != null && uri.toString().startsWith(res.getString(R.string.callback))) {
			new ReactiveAsyncTask<Uri, Void, TwitterAccount>(new Func1<Uri, TwitterAccount>() {
				@Override
				public TwitterAccount call(Uri _uri) {
					//AccessTokenの取得
					val verifier = _uri.getQueryParameter("oauth_verifier");
					AccessToken accessToken;
					try {
						accessToken = _oauth.getOAuthAccessToken(verifier);
					} catch (TwitterException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}

					//Accountを登録する
					val accountData = new TwitterAccount();
					accountData.setScreenName(accessToken.getScreenName());
					accountData.setAccessToken(accessToken.getToken());
					accountData.setAccessSecret(accessToken.getTokenSecret());
					accountData.setUse(true);
					accountData.setUserId(accessToken.getUserId());

					AccountDao.insert(accountData, getApplicationContext());

					return accountData;
				}
			}).setOnPostExecute(new Action1<TwitterAccount>() {
				@Override
				public void call(TwitterAccount x) {
					if(!_isNeedCallback) {
						Toast.makeText(getApplicationContext(), "OAuth認証が完了しました。", Toast.LENGTH_SHORT).show();
						val pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						pref.edit().putBoolean("isTweet", true).commit();
					} else {
						val data = new Intent();
						data.putExtra(KEY_ACCOUNT, x);
						setResult(RESULT_OK, data);
					}

					finish();
				}
			}).setOnError(new Action1<Exception>() {
				@Override
				public void call(Exception e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Oauth認証に失敗しました", Toast.LENGTH_LONG).show();
					if(_isNeedCallback) {
						setResult(RESULT_CANCELED);
					}
					finish();
				}
			}).execute(uri);

		}
	}

}
