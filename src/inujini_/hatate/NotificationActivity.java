/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import inujini_.hatate.data.TwitterAccount;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.function.Function.Predicate;
import inujini_.hatate.linq.Linq;
import inujini_.hatate.preference.EventableListPreference;
import inujini_.hatate.preference.EventableListPreference.OnChosenListener;
import inujini_.hatate.preference.ValidatableEditTextPreference;
import inujini_.hatate.preference.ValidatableEditTextPreference.TextValidator;
import inujini_.hatate.service.CallbackBroadcastReceiver;
import inujini_.hatate.service.OauthService;
import inujini_.hatate.service.RepeatYoService;
import inujini_.hatate.sqlite.dao.AccountDao;
import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.util.Util;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import twitter4j.auth.AccessToken;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * 通知詳細設定画面.
 */
@ExtensionMethod({PrefGetter.class, Linq.class})
public class NotificationActivity extends PreferenceActivity {

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
		addPreferencesFromResource(R.xml.notification);

		findPreference("isTweet").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if(!AccountDao.isAuthorized(getApplicationContext())) {

					new AlertDialog.Builder(NotificationActivity.this)
						.setTitle("確認")
						.setMessage("この機能を使用するにはOAuth認証が必要です。\n認証画面を表示しますか？")
						.setPositiveButton("はい", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();

								val prog = new ProgressDialog(NotificationActivity.this);
								prog.setMessage("OAuth認証画面を開いています。しばらくお待ちください。");
								prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

								if(_receiver != null) {
									unregisterReceiver(_receiver);
									_receiver = null;
								}

								_receiver = new CallbackBroadcastReceiver() {
									@Override
									public void onSuccess(AccessToken token) {
										if(prog != null && prog.isShowing())
											prog.dismiss();
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

										val p = (CheckBoxPreference) findPreference("isTweet");
										p.setChecked(true);
										_receiver = null;
									}

									@Override
									public void onError(Exception exception) {
										if(prog != null && prog.isShowing())
											prog.dismiss();
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
								prog.show();
							}
						}).setNegativeButton("キャンセル", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
						.create()
						.show();

					((CheckBoxPreference) preference).setChecked(false);

					return true;
				}

				return false;
			}
		});

		val lightColorPref = (ListPreference) findPreference("lightColor");
		lightColorPref.setSummary(getString(R.string.summary_light, getApplicationContext().getLightColorName()));
		lightColorPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(getString(R.string.summary_light
						, getApplicationContext().getLightColorName(newValue.toString())));
				return true;
			}
		});

		val snoozeTimePref = (ValidatableEditTextPreference) findPreference("snoozeTime");
		snoozeTimePref.setSummary(getString(R.string.summary_snooze, getApplicationContext().getSnoozeTime()));
		snoozeTimePref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		snoozeTimePref.setValidation("0以下の数値を入力することは出来ません。", new TextValidator() {
			@Override
			public boolean validation(String s) {
				if(s.equals("")) return false;

				val i = Integer.parseInt(s);
				return i > 0;
			}
		});
		snoozeTimePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(getString(R.string.summary_snooze, Integer.parseInt(newValue.toString())));
				return true;
			}
		});

		val vibrationPatternPref = (EventableListPreference) findPreference("vibrationPattern");
		vibrationPatternPref.setSummary(getString(R.string.summary_vibration
				, getApplicationContext().getVibrationPatternName()));
		vibrationPatternPref.setOnChosenListener(new OnChosenListener() {
			@Override
			public void onChosen(int index, String entry, String entryValue) {
				val vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(entryValue.split(",").linq().select(new Func1<String, Long>() {
					@Override
					public Long call(String x) {
						return Long.parseLong(x);
					}
				}).toLongArray(), -1);
			}
		});
		vibrationPatternPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(getString(R.string.summary_vibration
						, getApplicationContext().getVibrationPatternName(newValue.toString())));
				return true;
			}
		});

		val yoPref = findPreference("yo");

		// 既にYoを送っている場合は押させない
		if(!RepeatYoService.isSentYo(getApplicationContext())) {
			yoPref.setEnabled(false);
		}

		yoPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if(hasYo()) {
					return false;
				}

				val i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.yo_invate_uri)));
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
				return true;
			}
		});

		yoPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if(newValue == null || "".equals(newValue))
					return false;

				preference.setEnabled(false);
				Util.setRepeatYo(getApplicationContext());
				return true;
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Note: このアクティビティから抜けるときは必ず_receiverを解除しておく
		if (keyCode == KeyEvent.KEYCODE_BACK && _receiver != null) {
			unregisterReceiver(_receiver);
			_receiver = null;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean hasYo() {
		val yoPackage = getString(R.string.yo_package);
		val pm = getPackageManager();

		return pm.getInstalledApplications(0).linq().any(new Predicate<ApplicationInfo>(){
			@Override
			public Boolean call(ApplicationInfo x) {
				return x.packageName.equals(yoPackage);
			}
		});
	}
}
