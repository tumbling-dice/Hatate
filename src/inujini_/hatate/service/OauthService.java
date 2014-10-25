/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * <p>Oauth認証処理.</p>
 * <p>このServiceは以下の三つの機能を有する.</p>
 * <ol>
 *   <li>Intentに含まれたconsumer_keyとconsumer_secretから認証用URIを生成し、画面に表示する</li>
 *   <li>認証完了後、コールバックとして渡されたURIから{@link AccessToken}を抽出し、
 *       {@link CallbackBroadcastReceiver#onSuccess(AccessToken)}へ処理を委譲する</li>
 *   <li>処理中に何らかの例外が発生した場合は即座に{@link CallbackBroadcastReceiver#onError(Exception)}へ処理を委譲する</li>
 * </ol>
 */
public class OauthService extends IntentService {

	// use in start oauth
	public static final String KEY_CONSUMER_KEY = "consumerKey";
	public static final String KEY_CONSUMER_SECRET = "consumerSecret";
	private static final String URI_CALLBACK = "oauth://callback";

	// use in get access_token
	public static final String KEY_FINISHED_OAUTH = "isFinished";

	/**
	 * Oauth認証処理.
	 */
	public OauthService() {
		super("OauthService");
	}

	/**
	 * Oauth認証処理.
	 * @param name
	 */
	public OauthService(String name) {
		super(name);
	}

	/**
	 * OauthServiceへのIntentを作成.
	 * @param consumerKey
	 * @param consumerSecret
	 * @param context
	 * @return ExtraとしてconsumerKeyとconsumerSecretを持ったOauthServiceへのIntent
	 */
	public static Intent createIntent(String consumerKey, String consumerSecret, Context context) {
		Intent intent = new Intent(context, OauthService.class);
		intent.putExtra(KEY_CONSUMER_KEY, consumerKey);
		intent.putExtra(KEY_CONSUMER_SECRET, consumerSecret);

		return intent;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(!intent.getBooleanExtra(KEY_FINISHED_OAUTH, false))
			startOauth(intent);
		else
			getAccessToken(intent);
	}

	/**
	 * Oauth認証開始
	 * @param intent
	 */
	private void startOauth(Intent intent) {
		// validate
		if(!intent.hasExtra(KEY_CONSUMER_KEY) || !intent.hasExtra(KEY_CONSUMER_SECRET)) {
			IllegalStateException e
				= new IllegalStateException("In startOauth, intent's extra must have consumerKey and consumerSecret.");

			CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(e);
			sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
			return;
		}

		Configuration conf = new ConfigurationBuilder()
									.setOAuthConsumerKey(intent.getStringExtra(KEY_CONSUMER_KEY))
									.setOAuthConsumerSecret(intent.getStringExtra(KEY_CONSUMER_SECRET))
									.build();

		OAuthAuthorization oauth = new OAuthAuthorization(conf);
		oauth.setOAuthAccessToken(null);

		// 認証用URI作成
		String uri;
		try {
			uri = oauth.getOAuthRequestToken(URI_CALLBACK).getAuthorizationURL();
		} catch (TwitterException e) {
			e.printStackTrace();
			CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(e);
			sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
			return;
		}

		// OAuthAuthorization保存
		try {
			serialize(oauth, "oauth.dat");
		} catch (IOException e) {
			e.printStackTrace();
			CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(e);
			sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
			return;
		}

		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	/**
	 * AccessToken取得
	 * @param intent
	 */
	private void getAccessToken(Intent intent) {
		// validate
		if(intent.getData() == null) {
			IllegalStateException e
				= new IllegalStateException("OAuth is success but intent's data (URI) is null.");

			CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(e);
			sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
			return;
		}

		OAuthAuthorization oauth = null;

		// OAuthAuthorization復元
		try {
			oauth = deserialize("oauth.dat");
		} catch(Exception e) {
			e.printStackTrace();
			CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(e);
			sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
			return;
		}

		// AccessToken抽出
		String verifier = intent.getData().getQueryParameter("oauth_verifier");
		AccessToken accessToken;
		try {
			accessToken = oauth.getOAuthAccessToken(verifier);
		} catch (TwitterException e) {
			e.printStackTrace();
			CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(e);
			sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
			return;
		}

		CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(accessToken);
		sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
	}

	private void serialize(OAuthAuthorization obj, String fileName) throws IOException {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = openFileOutput(fileName, 0);
			oos = new ObjectOutputStream(fos);
 			oos.writeObject(obj);
		} finally {
			if(fos != null) {
				if(oos != null) oos.close();
				fos.close();
			}
		}
	}

	private OAuthAuthorization deserialize(String fileName) throws Exception {
		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = openFileInput(fileName);
			ois = new ObjectInputStream(fis);
			return (OAuthAuthorization) ois.readObject();
		} finally {
			if(fis != null) {
				if(ois != null) ois.close();
				fis.close();
			}

			// Note:残しておいても仕方ないので削除する
			deleteFile(fileName);
		}
	}
}