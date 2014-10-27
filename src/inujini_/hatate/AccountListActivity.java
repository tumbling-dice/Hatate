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
import inujini_.hatate.function.Function.Action;
import inujini_.hatate.function.Function.Action1;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import inujini_.hatate.service.CallbackBroadcastReceiver;
import inujini_.hatate.service.OauthService;
import inujini_.hatate.sqlite.dao.AccountDao;

import java.util.List;

import lombok.val;
import twitter4j.auth.AccessToken;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
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
 * Twitter連携に使用するアカウントの管理画面.
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

		val prog = new ProgressDialog(this);
		prog.setMessage("アカウント一覧を取得しています...");
		prog.setCancelable(false);
		prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		new ReactiveAsyncTask<Context, Void, List<TwitterAccount>>(new Func1<Context, List<TwitterAccount>>() {
			@Override
			public List<TwitterAccount> call(Context context) {
				return AccountDao.getAllAccount(context);
			}
		}).setOnPreExecute(new Action() {
			@Override
			public void call() {
				prog.show();
			}
		}).setOnPostExecute(new Action1<List<TwitterAccount>>() {
			@Override
			public void call(List<TwitterAccount> x) {
				if(prog != null && prog.isShowing())
					prog.dismiss();

				val lv = getListView();
				lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				lv.setOnItemLongClickListener(AccountListActivity.this);

				val adapter = new AccountAdapter(getApplicationContext(), x);
				setListAdapter(adapter);

				// set default checkbox's value
				for(int i = 0, count = adapter.getCount(); i < count; i++) {
					lv.setItemChecked(i, adapter.getItem(i).isUse());
				}

			}
		}).setOnError(new Action1<Exception>() {
			@Override
			public void call(Exception e) {
				e.printStackTrace();
				if(prog != null && prog.isShowing())
					prog.dismiss();

				Toast.makeText(getApplicationContext(), "取得に失敗しました。", Toast.LENGTH_SHORT).show();
			}
		}).execute(getApplicationContext());
	}

	@Override
	protected void onListItemClick (ListView l, View v, int position, long id) {
		val adapter = (AccountAdapter) getListAdapter();
		val item = adapter.getItem(position);
		item.setUse(!item.isUse());
		AccountDao.setUseFlagAsync(item, getApplicationContext());
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
					AccountDao.deleteAsync(item, getApplicationContext());
					adapter.remove(item);

					// 全アカウントが削除されたらTwitter連携を自動で切る
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

			val prog = new ProgressDialog(this);
			prog.setMessage("OAuth認証画面を開いています。しばらくお待ちください。");
			prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			_receiver = new CallbackBroadcastReceiver() {
				@Override
				public void onSuccess(AccessToken token) {
					_receiver = null;
					if(prog != null && prog.isShowing())
						prog.dismiss();

					if(AccountDao.isExist(token.getUserId(), getApplicationContext())) {
						Toast.makeText(getApplicationContext()
							, "既に登録されているアカウントは追加できません。"
							,Toast.LENGTH_SHORT).show();
						return;
					}

					Toast.makeText(getApplicationContext()
							, "OAuth認証が完了しました。\nブラウザが開きっぱなしの場合は閉じて下さい。"
							,Toast.LENGTH_SHORT).show();
					val accountData = new TwitterAccount();
					accountData.setScreenName(token.getScreenName());
					accountData.setAccessToken(token.getToken());
					accountData.setAccessSecret(token.getTokenSecret());
					accountData.setUse(true);
					accountData.setUserId(token.getUserId());

					AccountDao.insertAsync(accountData, getApplicationContext());
					((AccountAdapter) getListAdapter()).add(accountData);
				}

				@Override
				public void onError(Exception exception) {
					_receiver = null;
					if(prog != null && prog.isShowing())
						prog.dismiss();

					Toast.makeText(getApplicationContext(), "Oauth認証に失敗しました"
							, Toast.LENGTH_LONG).show();
				}
			};

			registerReceiver(_receiver, CallbackBroadcastReceiver.createIntentFilter());

			// Oauth認証開始
			val res = getApplicationContext().getResources();
			startService(OauthService.createIntent(res.getString(R.string.consumer_key)
					, res.getString(R.string.consumer_secret), getApplicationContext()));
			prog.show();

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