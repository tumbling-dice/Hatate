package inujini_.hatate;

import inujini_.hatate.preference.TimePickerPreference;
import inujini_.hatate.sqlite.dao.StatisticsDao;
import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.util.Util;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
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

		val timePref = (TimePickerPreference)findPreference("time");

		timePref.setSummary(getString(R.string.summary_time
			, getApplicationContext().getHour(), getApplicationContext().getMinute()));

		timePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				val timePicker = (TimePicker) newValue;
				val pref = findPreference("time");
				pref.setSummary(getString(R.string.summary_time
						, timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
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

		// check db state
		Util.dbUpdateAsync(this);

		// 設定画面起動時にアラーム設定を削除する
		Util.removeAlarm(getApplicationContext());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& getApplicationContext().isNoisy()) {
			Util.setAlarm(getApplicationContext());
		}
		return super.onKeyDown(keyCode, event);
	}
}
