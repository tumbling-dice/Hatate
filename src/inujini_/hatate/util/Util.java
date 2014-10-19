package inujini_.hatate.util;

import inujini_.function.Function.Action;
import inujini_.function.Function.Action1;
import inujini_.function.Function.Func1;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import inujini_.hatate.service.Houtyou;
import inujini_.hatate.service.OneMoreLovely;
import inujini_.hatate.sqlite.DatabaseHelper;
import inujini_.linq.Linq;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.Calendar;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

@ExtensionMethod({PrefGetter.class, Linq.class})
public class Util {

	private static SoftReference<AlarmManager> _alarmManager;

	@SuppressLint("DefaultLocale")
	public static long getAlermTime(int hour, int minute) {
		val calendar = Calendar.getInstance();

		val currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		val currentMinute = calendar.get(Calendar.MINUTE);

		if(Integer.parseInt(String.format("%02d%02d", hour, minute))
				<= Integer.parseInt(String.format("%02d%02d", currentHour, currentMinute))) {
			calendar.set(Calendar.DAY_OF_MONTH, (calendar.get(Calendar.DAY_OF_MONTH) + 1));
		}

		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTimeInMillis();
	}

	public static void setAlarm(Context context) {
		getAlarmManager(context).setRepeating(AlarmManager.RTC_WAKEUP
				, getAlermTime(context.getHour(), context.getMinute())
				, AlarmManager.INTERVAL_DAY, getAlarmIntent(context));
	}

	public static void removeAlarm(Context context) {
		getAlarmManager(context).cancel(getAlarmIntent(context));
	}

	public static PendingIntent getAlarmIntent(Context context) {
		return PendingIntent.getBroadcast(context, -1, new Intent(context, Houtyou.class)
			, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	public static void setSnooze(Context context) {
		getAlarmManager(context).set(AlarmManager.RTC_WAKEUP
				, (System.currentTimeMillis() + context.getSnoozeTimeMill())
				, getSnoozeIntent(context));
	}

	public static void removeSnooze(Context context) {
		getAlarmManager(context).cancel(getSnoozeIntent(context));
	}

	public static PendingIntent getSnoozeIntent(Context context) {
		return PendingIntent.getBroadcast(context, -1, new Intent(context, OneMoreLovely.class)
				, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	public static AlarmManager getAlarmManager(Context context) {
		AlarmManager am = null;
		if(_alarmManager != null) {
			am = _alarmManager.get();
		}

		if(am == null) {
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			_alarmManager = new SoftReference<AlarmManager>(am);
		}

		return am;
	}

	public static InputStreamReader getAssetStream(Context context, String fileName) throws IOException {
		val asm = context.getResources().getAssets();
		return new InputStreamReader(asm.open(fileName));
	}


	public static void dbUpdateAsync(final Context context) {
		if(!DatabaseHelper.isDbOpened(context)
			|| !DatabaseHelper.isDbUpdated(context)) {

			val prog = new ProgressDialog(context);
			prog.setTitle("DB Update");
			prog.setMessage("内部データベースをUpdateしています...");
			prog.setCancelable(false);
			prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			new ReactiveAsyncTask<Context, Void, Void>(new Func1<Context, Void>() {
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
				}
			}).setOnError(new Action1<Exception>() {
				@Override
				public void call(Exception e) {
					if(prog != null && prog.isShowing())
						prog.dismiss();

					if(e != null) e.printStackTrace();
					Toast.makeText(context, "エラーが発生しました。", Toast.LENGTH_SHORT).show();

				}
			}).execute(context);
		}
	}

	public static void dbUpdate(Context context) {
		if(!DatabaseHelper.isDbOpened(context)
				|| !DatabaseHelper.isDbUpdated(context)) {
			val d = new DatabaseHelper(context);
			d.getWritableDatabase().close();
			d.close();
		}
	}

}
