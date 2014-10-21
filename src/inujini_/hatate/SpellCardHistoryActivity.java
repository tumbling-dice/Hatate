/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import inujini_.function.Function.Action;
import inujini_.function.Function.Action1;
import inujini_.function.Function.Func1;
import inujini_.hatate.adapter.SpellCardHistoryAdapter;
import inujini_.hatate.data.SpellCardHistory;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import inujini_.hatate.sqlite.dao.SpellCardDao;

import java.util.List;

import lombok.val;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

/**
 * スペルカード履歴一覧画面.
 */
public class SpellCardHistoryActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		val prog = new ProgressDialog(this);
		prog.setTitle("データ取得");
		prog.setMessage("データ取得中...");
		prog.setCancelable(false);
		prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		new ReactiveAsyncTask<Context, Void, List<SpellCardHistory>>(new Func1<Context, List<SpellCardHistory>>() {
			@Override
			public List<SpellCardHistory> call(Context context) {
				return SpellCardDao.getHistory(context);
			}
		}).setOnPreExecute(new Action() {
			@Override
			public void call() {
				prog.show();
			}
		}).setOnPostExecute(new Action1<List<SpellCardHistory>>() {
			@Override
			public void call(List<SpellCardHistory> x) {
				if(prog != null && prog.isShowing())
					prog.dismiss();

				if(x != null)
					setListAdapter(new SpellCardHistoryAdapter(getApplicationContext(), x));
				else
					Toast.makeText(getApplicationContext(), "データがありませんでした。", Toast.LENGTH_SHORT).show();
			}
		}).setOnError(new Action1<Exception>() {
			@Override
			public void call(Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "何らかのエラーが発生しました。", Toast.LENGTH_SHORT).show();
			}
		}).execute(getApplicationContext());
	}

}
