/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import inujini_.hatate.service.OauthService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Oauth認証コールバック中継地点.
 */
public class OauthActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(getApplicationContext(), OauthService.class);
		intent.setData(getIntent().getData());
		startService(intent);
		finish();
	}

}
