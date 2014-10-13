package inujini_.hatate;

import inujini_.hatate.preference.TimePickerPreference;
import inujini_.hatate.preference.ValidatableEditTextPreference;
import inujini_.hatate.preference.ValidatableEditTextPreference.TextValidator;
import inujini_.hatate.service.Houtyou;
import inujini_.hatate.sqlite.DatabaseHelper;
import inujini_.hatate.sqlite.dao.AccountDao;
import inujini_.hatate.sqlite.dao.StatisticsDao;
import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.util.Util;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.TimePicker;
import android.widget.Toast;

@ExtensionMethod({PrefGetter.class})
public class MainActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main);

		// check db state
		if(!DatabaseHelper.isDbOpened(getApplicationContext())
			|| !DatabaseHelper.isDbUpdated(getApplicationContext())) {
			val d = new DatabaseHelper(getApplicationContext());
			d.getWritableDatabase().close();
			d.close();
		}

		val timePref = (TimePickerPreference)findPreference("time");

		timePref.setSummary(String.format(timePref.getSummary().toString()
				, getApplicationContext().getHour(), getApplicationContext().getMinute()));

		timePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				val timePicker = (TimePicker) newValue;
				val pref = findPreference("time");
				pref.setSummary(String.format("包丁で刺される時刻を設定します。\n現在は%02d:%02dに設定されています。"
						, timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
				return true;
			}
		});

		findPreference("preview").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				val intent = new Intent();
				intent.putExtra(Houtyou.KEY_IS_PREVIEW, true);
				new Houtyou().onReceive(getApplicationContext(), intent);
				return false;
			}
		});

		findPreference("license").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(getApplicationContext(), LicenseActivity.class));
				return false;
			}
		});

		findPreference("isTweet").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if(!AccountDao.isAuthorized(getApplicationContext())) {

					new AlertDialog.Builder(MainActivity.this)
						.setTitle("確認")
						.setMessage("この機能を使用するにはOAuth認証が必要です。\n認証画面を表示しますか？")
						.setPositiveButton("はい", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								val intent = new Intent(getApplicationContext(), OauthActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
		lightColorPref.setSummary(String.format(lightColorPref.getSummary().toString()
				, getApplicationContext().getLightColorName()));
		lightColorPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(String.format("点灯するLEDの色を設定します。\n現在の色は%sです。"
						, PrefGetter.getLightColorName(newValue.toString())));
				return true;
			}
		});

		val snoozeTimePref = (ValidatableEditTextPreference) findPreference("snoozeTime");
		snoozeTimePref.setSummary(String.format(snoozeTimePref.getSummary().toString()
				, getApplicationContext().getSnoozeTime()));
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
				preference.setSummary(String.format("スヌーズの間隔を設定します。(単位：秒)\n現在は%s秒に設定されています。"
						, newValue));
				return true;
			}
		});

		findPreference("love").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				val statistics = StatisticsDao.getStatistics(getApplicationContext());

				val toast = new Toast(getApplicationContext());
				toast.setView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_love, null));
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setText(String.format("刺した回数：%d\n好感度：%d"
						, statistics.getCount(), statistics.getLove()));
				toast.show();
				return false;
			}
		});

		// 設定画面起動時にアラーム設定を削除する
		Util.removeAlarm(getApplicationContext());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && getApplicationContext().isNoisy()) {
			Util.setAlarm(getApplicationContext());
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		val pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		((CheckBoxPreference) findPreference("isTweet")).setChecked(pref.getBoolean("isTweet", false));
	}




}
