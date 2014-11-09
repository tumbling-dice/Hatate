/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import inujini_.hatate.function.Function.Action;
import inujini_.hatate.function.Function.Action1;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.love.Love;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import inujini_.hatate.sqlite.dao.SpellCardDao;
import inujini_.hatate.sqlite.dao.StatisticsDao;
import inujini_.hatate.util.IconUtil;
import inujini_.hatate.util.PrefGetter;

import java.util.ArrayList;
import java.util.List;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * スペルカードガチャ画面.
 */
@ExtensionMethod({PrefGetter.class})
public class GachaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gacha);
	}

	public void gacha(View v) {
		val context = getApplicationContext();

		if(context.isVibration()) {
			val vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibe.vibrate(context.getVibrationPattern(), -1);
		}

		if(context.isScream()) {
			val statistics = StatisticsDao.getStatistics(context);
			val mp = MediaPlayer.create(context, Love.getVoice(statistics.getLove()));
			mp.seekTo(0);
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer x) {
					x.release();
				}
			});
		}

		val prog = new ProgressDialog(this);
		prog.setMessage("結果を取得しています...");
		prog.setCancelable(false);
		prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		new ReactiveAsyncTask<Context, Void, List<TextView>>(new Func1<Context, List<TextView>>() {
			@Override
			public List<TextView> call(Context c) {
				val textViews = new ArrayList<TextView>();

				for(int i = 0; i < 3; i++) {
					val spellCard = SpellCardDao.getRandomSpellCard(c);
					val textView = cloneToastTextView(getApplicationContext());
					val d = IconUtil.getIconDrawable(getApplicationContext(), spellCard.getCharacterId());
					d.setBounds(new Rect(0, 0, 64, 64));
					textView.setCompoundDrawables(d, null, null, null);
					textView.setCompoundDrawablePadding(50);
					textView.setText(spellCard.getName());
					textView.setId((int) spellCard.getId());
					textView.setGravity(Gravity.CENTER_VERTICAL);
					textView.setPadding(0, 0, 0, 15);
					textViews.add(textView);
				}

				val pref = PreferenceManager.getDefaultSharedPreferences(c);
				pref.edit().putBoolean(MainActivity.KEY_GACHA, false).commit();

				return textViews;
			}
		}).setOnPreExecute(new Action() {
			@Override
			public void call() {
				prog.show();
			}
		}).setOnPostExecute(new Action1<List<TextView>>() {
			@Override
			public void call(List<TextView> x) {
				if(prog != null && prog.isShowing())
					prog.dismiss();

				val t = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_LONG);
				val toastView = (ViewGroup) t.getView();
				toastView.removeAllViews();

				for (val textView : x) {
					toastView.addView(textView);
				}

				t.show();

				setResult(RESULT_OK);
				finish();
			}
		}).execute(getApplicationContext());
	}

	private LayoutInflater _inflater;
	private int _toastViewId;
	private TextView cloneToastTextView(Context context) {
		if(_inflater == null) {
			_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			_toastViewId = Resources.getSystem().getIdentifier("transient_notification", "layout", "android");
		}
		val v = (ViewGroup) _inflater.inflate(_toastViewId, null);
		val textView = (TextView) v.findViewById(android.R.id.message);
		v.removeView(textView);
		return textView;
	}
}
