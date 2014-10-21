/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import inujini_.function.Function.Func1;
import inujini_.hatate.preference.EventableListPreference;
import inujini_.hatate.preference.EventableListPreference.OnChosenListener;
import inujini_.hatate.preference.ValidatableEditTextPreference;
import inujini_.hatate.preference.ValidatableEditTextPreference.TextValidator;
import inujini_.hatate.sqlite.dao.AccountDao;
import inujini_.hatate.util.PrefGetter;
import inujini_.linq.Linq;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.InputType;

/**
 * 通知詳細設定画面.
 */
@ExtensionMethod({PrefGetter.class, Linq.class})
public class NotificationActivity extends PreferenceActivity {

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
								val intent = new Intent(getApplicationContext(), OauthActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								startActivity(intent);
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

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		val pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		((CheckBoxPreference) findPreference("isTweet")).setChecked(pref.getBoolean("isTweet", false));
	}

}
