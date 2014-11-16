/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import inujini_.hatate.preference.TimePickerPreference;
import inujini_.hatate.sqlite.dao.StatisticsDao;
import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.util.Util;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 初期表示画面.
 */
@ExtensionMethod({PrefGetter.class})
public class MainActivity extends PreferenceActivity {

	private static final int REQ_GACHA = 100;
	public static final String KEY_GACHA = "canGacha";

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

		val gachaPref = findPreference("gacha");
		gachaPref.setEnabled(PrefGetter.canGacha(getApplicationContext()));
		gachaPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivityForResult(new Intent(getApplicationContext(), GachaActivity.class), REQ_GACHA);
				return false;
			}
		});

		findPreference("help").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				val intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://tumbling-dice.github.io/Hatate/"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return false;
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		// 設定画面起動時にアラーム設定を削除する
		Util.removeAlarm(getApplicationContext());
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		// Note: Activity#onUserLeaveHintをオーバーライドすることで
		// HOMEキーが押されたときの処理をフックできる。
		if(getApplicationContext().isNoisy()) {
			Util.setAlarm(getApplicationContext());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK
				&& getApplicationContext().isNoisy()) {
			Util.setAlarm(getApplicationContext());
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != REQ_GACHA) {
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}

		if(resultCode != RESULT_OK)
			return;

		findPreference("gacha").setEnabled(false);
	}


}
