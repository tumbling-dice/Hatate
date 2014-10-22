/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import inujini_.hatate.adapter.AccountAdapter;
import inujini_.hatate.data.TwitterAccount;
import inujini_.hatate.service.CallbackBroadcastReceiver;
import inujini_.hatate.service.OauthService;
import inujini_.hatate.sqlite.dao.AccountDao;
import lombok.val;
import twitter4j.auth.AccessToken;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 *
 */
public class AccountListActivity extends ListActivity implements OnItemLongClickListener {

	/**
	 * <p>Oauth認証用レシーバ.<p>
	 * <p>使い終わった後は必ずnullにする</p>
	 * <p>画面を閉じる際にnullになっていなかったら
	 * {@link Context#unregisterReceiver(android.content.BroadcastReceiver)}を呼ぶこと</p>
	 */
	private CallbackBroadcastReceiver _receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		val context = getApplicationContext();
		val lv = super.getListView();
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lv.setOnItemLongClickListener(this);

		setListAdapter(new AccountAdapter(context, AccountDao.getAllAccount(context)));
	}

	@Override
	protected void onListItemClick (ListView l, View v, int position, long id) {
		val adapter = (AccountAdapter) getListAdapter();
		val item = adapter.getItem(position);
		item.setUse(!item.isUse());
		AccountDao.update(item, getApplicationContext());
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		val adapter = (AccountAdapter) getListAdapter();
		val item = adapter.getItem(position);

		new AlertDialog.Builder(this)
			.setTitle("確認")
			.setMessage(String.format("%sを削除します。よろしいですか？", item.getScreenName()))
			.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					AccountDao.delete(item, getApplicationContext());
					adapter.remove(item);
					if(adapter.getCount() == 0) {
						PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
							.edit().putBoolean("isTweet", false).commit();
					}
				}
			}).setNegativeButton("キャンセル", new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).create().show();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_account_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_item_add:
			if(_receiver != null) {
				unregisterReceiver(_receiver);
				_receiver = null;
			}

			_receiver = new CallbackBroadcastReceiver() {
				@Override
				public void onSuccess(AccessToken token) {
					Toast.makeText(getApplicationContext()
							, "OAuth認証が完了しました。\nブラウザが開きっぱなしの場合は閉じて下さい。",
							Toast.LENGTH_SHORT).show();
					val accountData = new TwitterAccount();
					accountData.setScreenName(token.getScreenName());
					accountData.setAccessToken(token.getToken());
					accountData.setAccessSecret(token.getTokenSecret());
					accountData.setUse(true);
					accountData.setUserId(token.getUserId());

					AccountDao.insert(accountData, getApplicationContext());
					_receiver = null;

					val adapter = (AccountAdapter) getListAdapter();
					adapter.add(accountData);
				}

				@Override
				public void onError(Exception exception) {
					Toast.makeText(getApplicationContext(), "Oauth認証に失敗しました"
							, Toast.LENGTH_LONG).show();
					_receiver = null;
				}
			};

			registerReceiver(_receiver, CallbackBroadcastReceiver.createIntentFilter());

			// Oauth認証開始
			val res = getApplicationContext().getResources();
			startService(OauthService.createIntent(res.getString(R.string.consumer_key)
					, res.getString(R.string.consumer_secret), getApplicationContext()));
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && _receiver != null) {
			unregisterReceiver(_receiver);
			_receiver = null;
		}
		return super.onKeyDown(keyCode, event);
	}

}