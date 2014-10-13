package inujini_.hatate.service;

import inujini_.hatate.util.Util;
import android.content.Context;
import android.content.Intent;

public class OneMoreLovely extends PierceReceiver {

	@Override
	public void onPierced(Context context, Intent intent) {
		Util.setSnooze(context);
	}

}
