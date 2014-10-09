package inujini_.hatate;

import inujini_.hatate.preference.TimePickerPreference;
import inujini_.hatate.service.Houtyou;
import inujini_.hatate.twitter.TwitterAccountDao;
import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.util.TimeUtil;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

@ExtensionMethod({PrefGetter.class})
public class MainActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main);

		val timePref = (TimePickerPreference)findPreference("time");

		timePref.setSummary(String.format(timePref.getSummary().toString()
				, getApplicationContext().getHour(), getApplicationContext().getMinute()));

		timePref.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				val pref = findPreference("time");
				pref.setSummary(String.format("包丁で刺される時刻を設定します。\n現在は%02d:%02dに設定されています。", hourOfDay, minute));

			}
		});

		/*findPreference("screamDetail").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(getApplicationContext(), VoiceSettingActivity.class));
				return false;
			}
		});*/

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
				if(!TwitterAccountDao.isAuthorized(getApplicationContext())) {

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
				return false;
			}
		});

		// 設定画面起動時にアラーム設定を削除する
		val context = getBaseContext();
		val intent = new Intent(context, Houtyou.class);

		val pendingIntent = PendingIntent.getBroadcast(context, -1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		val alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && getApplicationContext().isNoisy()) {
			TimeUtil.setAlerm(getBaseContext());
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
