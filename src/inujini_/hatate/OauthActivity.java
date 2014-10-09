package inujini_.hatate;

import inujini_.function.Function.Action;
import inujini_.function.Function.Action1;
import inujini_.function.Function.Func1;
import inujini_.hatate.data.TwitterAccount;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import inujini_.hatate.twitter.TwitterAccountDao;
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

public class OauthActivity extends Activity {

	private OAuthAuthorization _oauth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
			new ReactiveAsyncTask<Uri, Void, Void>(new Func1<Uri, Void>() {
				@Override
				public Void call(Uri _uri) {
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

					TwitterAccountDao.insert(accountData, getApplicationContext());

					return null;
				}
			}).setOnPostExecute(new Action1<Void>() {
				@Override
				public void call(Void x) {
					Toast.makeText(getApplicationContext(), "OAuth認証が完了しました。", Toast.LENGTH_SHORT).show();
					val pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					pref.edit().putBoolean("isTweet", true).commit();
					finish();
				}
			}).setOnError(new Action1<Exception>() {
				@Override
				public void call(Exception e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Oauth認証に失敗しました", Toast.LENGTH_LONG).show();
					finish();
				}
			}).execute(uri);

		}
	}

}
