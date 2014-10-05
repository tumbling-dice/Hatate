package inujini_.hatate.service;

import inujini_.hatate.util.PrefGetter;
import inujini_.hatate.util.TimeUtil;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

@ExtensionMethod({PrefGetter.class})
public class UpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent == null || !context.isNoisy()) return;

		Log.d("UpdateReceiver", "onReceive");

		val action = intent.getAction();
		val packagePath = intent.getDataString();

		if(packagePath == null) return;

		if(Intent.ACTION_PACKAGE_REPLACED.equals(action)
				&& packagePath.equals("package:" + context.getPackageName())) {
			Log.d("UpdateReceiver", "this intent is PACKAGE_REPLACED");
			TimeUtil.setAlerm(context);
		}

	}

}
