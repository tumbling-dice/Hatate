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
import inujini_.hatate.reactive.ReactiveAsyncTask;
import inujini_.hatate.sqlite.DatabaseHelper;
import lombok.val;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 *
 */
public class OnStartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// check db state
		dbUpdateAsync(new Action1<Void>() {
			@Override
			public void call(Void x) {
				// check current version
				AppHatate.getRequestQueue(getApplicationContext())
					.add(new StringRequest("https://tumbling-dice.github.io/Hatate/ver.txt", new Listener<String>() {
						@Override
						public void onResponse(String response) {
							val context = getApplicationContext();
							val pm = context.getPackageManager();
							try {
								val packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
								if(packageInfo.versionCode < Integer.parseInt(response)) {
									new AlertDialog.Builder(OnStartActivity.this)
										.setTitle("アップデート")
										.setMessage("新しいバージョンが公開されています。\nインストールしますか？")
										.setPositiveButton("確認", new OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												val intent = new Intent(Intent.ACTION_VIEW
													, Uri.parse("https://tumbling-dice.github.io/Hatate/apk/Hatate.apk"));
												intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
												startActivity(intent);
											}
										}).setCancelable(false)
										.setNegativeButton("キャンセル", new OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												startMainActivity();
											}
										}).create().show();

									return;
								}
							} catch (NameNotFoundException e) {
								e.printStackTrace();
							}

							startMainActivity();
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							startMainActivity();
						}
					}));
			}
		});


	}

	private void dbUpdateAsync(final Action1<Void> callback) {

		if(DatabaseHelper.isDbOpened(getApplicationContext())
				&& DatabaseHelper.isDbUpdated(getApplicationContext())) {
			callback.call(null);
			return;
		}

		val prog = new ProgressDialog(this);
		prog.setTitle("DB Update");
		prog.setMessage("内部データベースを更新しています...");
		prog.setCancelable(false);
		prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		ReactiveAsyncTask.create(new Func1<Context, Void>() {
			@Override
			public Void call(Context c) {
				val d = new DatabaseHelper(c);
				d.getWritableDatabase().close();
				d.close();
				return null;
			}
		}).setOnPreExecute(new Action() {
			@Override
			public void call() {
				prog.show();
			}
		}).setOnPostExecute(new Action1<Void>() {
			@Override
			public void call(Void arg0) {
				if(prog != null && prog.isShowing())
					prog.dismiss();

				callback.call(arg0);
			}
		}).setOnError(new Action1<Exception>() {
			@Override
			public void call(Exception e) {
				if(prog != null && prog.isShowing())
					prog.dismiss();

				if(e != null) e.printStackTrace();
				Toast.makeText(getApplicationContext(), "エラーが発生しました。", Toast.LENGTH_SHORT).show();

			}
		}).execute(getApplicationContext());


	}

	private void startMainActivity() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
	}
}
