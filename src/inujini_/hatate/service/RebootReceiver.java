package inujini_.hatate.service;

import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.util.Util;
import lombok.experimental.ExtensionMethod;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

@ExtensionMethod({PrefGetter.class})
public class RebootReceiver extends AsyncBroadcastReceiver {

	@Override
	protected void asyncOnReceive(Context context, Intent intent) {
		if(intent == null || !context.isNoisy()) return;

		Log.d("RebootReceiver", "onReceive");

		if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Log.d("RebootReceiver", "this intent is BOOT_COMPLETED");
			Util.setAlarm(context);
		}

	}

}
