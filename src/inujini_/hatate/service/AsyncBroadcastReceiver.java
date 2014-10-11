package inujini_.hatate.service;

import inujini_.function.Function.Func1;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class AsyncBroadcastReceiver extends BroadcastReceiver {

	@Override
	public final void onReceive(Context context, final Intent intent) {
		new ReactiveAsyncTask<Context, Void, Void>(new Func1<Context, Void>() {
			@Override
			public Void call(Context x) {
				asyncOnReceive(x, intent);
				return null;
			}
		}).execute(context);
	}

	protected abstract void asyncOnReceive(Context context, Intent intent);

}
