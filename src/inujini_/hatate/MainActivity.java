/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import inujini_.hatate.data.SpellCard;
import inujini_.hatate.function.Function.Action;
import inujini_.hatate.function.Function.Action1;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.preference.TimePickerPreference;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import inujini_.hatate.sqlite.dao.SpellCardDao;
import inujini_.hatate.sqlite.dao.StatisticsDao;
import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.util.Util;

import java.util.ArrayList;
import java.util.List;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != REQ_GACHA) {
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}

		if(resultCode != RESULT_OK)
			return;

		val prog = new ProgressDialog(this);
		prog.setMessage("結果を取得しています...");
		prog.setCancelable(false);
		prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		new ReactiveAsyncTask<Context, Void, List<SpellCard>>(new Func1<Context, List<SpellCard>>() {
			@Override
			public List<SpellCard> call(Context context) {
				val cardList = new ArrayList<SpellCard>();

				for(int i = 0; i < 3; i++) {
					cardList.add(SpellCardDao.getRandomSpellCard(context));
				}

				val pref = PreferenceManager.getDefaultSharedPreferences(context);
				pref.edit().putBoolean(KEY_GACHA, false).commit();

				return cardList;
			}
		}).setOnPreExecute(new Action() {
			@Override
			public void call() {
				prog.show();
			}
		}).setOnPostExecute(new Action1<List<SpellCard>>() {
			@Override
			public void call(List<SpellCard> x) {
				if(prog != null && prog.isShowing())
					prog.dismiss();

				findPreference("gacha").setEnabled(false);

				val sb = new StringBuilder();

				for (val spellCard : x) {
					sb.append(spellCard.getName()).append('\n');
				}

				Toast.makeText(getApplicationContext(), String.format("%sを取得しました！", sb.toString())
						, Toast.LENGTH_SHORT).show();

			}
		}).execute(getApplicationContext());
	}
}
