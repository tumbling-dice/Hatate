package inujini_.hatate;

import inujini_.hatate.preference.TimePickerPreference;
import inujini_.hatate.service.Houtyou;
import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.util.TimeUtil;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
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

		timePref.setSummary(String.format("包丁で刺される時刻を設定します。\n現在は%02d:%02dに設定されています。"
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
				new Houtyou().onReceive(getApplicationContext(), null);
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

}
