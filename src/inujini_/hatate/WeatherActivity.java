/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import inujini_.hatate.adapter.WeatherAdapter;
import inujini_.hatate.volley.weather.WeatherManager;
import inujini_.hatate.volley.weather.WeatherResponse;
import lombok.val;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class WeatherActivity extends ListActivity {

	private static final String TAG = "WeatherActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		val prog = new ProgressDialog(this);
		prog.setTitle("データ取得");
		prog.setMessage("お天気情報取得中...");
		prog.setCancelable(false);
		prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prog.show();

		new WeatherManager(getApplicationContext()).getWeakly(new Listener<WeatherResponse>() {
			@Override
			public void onResponse(WeatherResponse response) {
				if(prog != null && prog.isShowing()) prog.dismiss();
				setTitle(String.format("はたてちゃん天気予報 - %s", response.getName()));
				setListAdapter(new WeatherAdapter(getApplicationContext(), response.getDatas()));
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if(prog != null && prog.isShowing()) prog.dismiss();
				if(error.networkResponse == null) {
					Log.d(TAG
						, String.format("error weather report. message:%s", error.getMessage()));
				} else {
					Log.d(TAG
						, String.format("error weather report. statuscode:%d message:%s"
							, error.networkResponse.statusCode
							, error.getMessage()));
				}

				Toast.makeText(getApplicationContext(), "お天気情報の取得に失敗しました。", Toast.LENGTH_SHORT).show();
			}
		});
	}

}
