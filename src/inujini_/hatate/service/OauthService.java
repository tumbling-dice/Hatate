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
 *
 */
public class OauthService extends IntentService {

	public static final String KEY_CONSUMER_KEY = "consumerKey";
	public static final String KEY_CONSUMER_SECRET = "consumerSecret";

	public OauthService() {
		super("OauthService");
	}

	public OauthService(String name) {
		super(name);
	}

	public static Intent createIntent(String consumerKey, String consumerSecret, Context context) {
		Intent intent = new Intent(context, OauthService.class);
		intent.putExtra(KEY_CONSUMER_KEY, consumerKey);
		intent.putExtra(KEY_CONSUMER_SECRET, consumerSecret);

		return intent;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(intent.hasExtra(KEY_CONSUMER_KEY)){
			startOauth(intent);
		} else {
			if(intent.getData() == null) {
				throw new IllegalStateException();
			}
			getAccessToken(intent);
		}
	}

	/**
	 * Oauth認証開始
	 * @param intent
	 */
	private void startOauth(Intent intent) {
		String consumerKey = intent.getStringExtra(KEY_CONSUMER_KEY);
		String consumerSecret = intent.getStringExtra(KEY_CONSUMER_SECRET);
		String callbackUri = "oauth://callback";

		Configuration conf = new ConfigurationBuilder()
									.setOAuthConsumerKey(consumerKey)
									.setOAuthConsumerSecret(consumerSecret)
									.build();

		OAuthAuthorization oauth = new OAuthAuthorization(conf);
		oauth.setOAuthAccessToken(null);

		String uri;
		try {
			uri = oauth.getOAuthRequestToken(callbackUri).getAuthorizationURL();
		} catch (TwitterException e) {
			e.printStackTrace();
			CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(e);
			sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
			return;
		}

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
		OAuthAuthorization oauth = null;

		try {
			oauth = deserialize("oauth.dat");
		} catch(Exception e) {
			e.printStackTrace();
			CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(e);
			sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
			return;
		}

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

			deleteFile(fileName);
		}
	}
}