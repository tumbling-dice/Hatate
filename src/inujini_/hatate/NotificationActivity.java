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
public class NotificationActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.notification);

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
		lightColorPref.setSummary(getString(R.stirng.summary_light, getApplicationContext().getLightColorName()));
		lightColorPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(getString(R.string.summary_light, PrefGetter.getLightColorName(newValue.toString())));
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
				preference.setSummary(getString(R.string.summary_snooze, newValue));
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
